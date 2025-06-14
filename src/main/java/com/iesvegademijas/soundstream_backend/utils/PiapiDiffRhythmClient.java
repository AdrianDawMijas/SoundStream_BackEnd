package com.iesvegademijas.soundstream_backend.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP para interactuar con la API externa de generación musical de PIAPI.AI.
 * Utiliza dos modelos: uno primario ("music-u") y otro de respaldo ("Qubico/diffrhythm").
 */
@Component
public class PiapiDiffRhythmClient {

    private static final String API_KEY = "416c8ff7b948328707a0e24a85f98f0a8b28a7ceab6c5d5455f81fb4339d9e4e";
    private static final String SUBMIT_URL = "https://api.piapi.ai/api/v1/task";
    private static final String GET_URL_BASE = "https://api.piapi.ai/api/v1/task/";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Inicia el proceso de generación musical utilizando un prompt de descripción.
     * Primero intenta con el modelo "music-u". Si falla, recurre a "Qubico/diffrhythm".
     *
     * @param gptPrompt Descripción generada (por IA o manualmente) del estilo de música deseado.
     * @return Objeto con título y URL de la canción generada.
     */
    public GeneratedSongResult submitSongGeneration(String gptPrompt) {
        try {
            System.out.println("🎯 Intentando modelo music-u...");
            String taskId = submitGenerationTask(gptPrompt);
            GeneratedSongResult result = pollForAudioUrl(taskId);
            if (result.getUrl() == null || result.getUrl().isBlank()) {
                throw new RuntimeException("⚠️ Modelo music-u no devolvió resultado válido.");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("⚠️ Falló music-u, usando modelo Qubico/diffrhythm...");
            try {
                return submitWithDiffRhythm(gptPrompt);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("🚨 Ambos modelos fallaron.");
            }
        }
    }

    /**
     * Envia una solicitud de generación musical al modelo "music-u".
     *
     * @param gptPrompt Prompt de descripción.
     * @return Identificador de tarea devuelto por la API.
     */
    private String submitGenerationTask(String gptPrompt) throws IOException, InterruptedException {
        String requestBody = """
            {
              "model": "music-u",
              "task_type": "generate_music",
              "input": {
                "gpt_description_prompt": "%s",
                "negative_tags": "",
                "lyrics_type": "generate",
                "seed": -1
              },
              "config": {
                "service_mode": "public",
                "webhook_config": {
                  "endpoint": "",
                  "secret": ""
                }
              }
            }
            """.formatted(gptPrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBMIT_URL))
                .header("X-API-Key", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 && response.body().contains("task_id")) {
            String body = response.body();
            int index = body.indexOf("\"task_id\":\"") + 11;
            int end = body.indexOf("\"", index);
            return body.substring(index, end);
        }

        throw new RuntimeException("❌ Error en submitGenerationTask");
    }

    /**
     * Realiza un polling periódico hasta obtener el resultado de una canción generada por el modelo "music-u".
     *
     * @param taskId ID de la tarea.
     * @return Resultado con título y URL de la canción.
     */
    private GeneratedSongResult pollForAudioUrl(String taskId) throws IOException, InterruptedException {
        String url = GET_URL_BASE + taskId;

        for (int i = 0; i < 30; i++) {
            Thread.sleep(10000); // Espera 10 segundos entre cada intento

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-API-Key", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (body.contains("\"status\":\"failed\"")) {
                throw new RuntimeException("❌ La generación falló.");
            }

            if (body.contains("\"status\":\"completed\"") && body.contains("\"song_path\"")) {
                int indexStart = body.indexOf("\"song_path\":\"") + 13;
                int indexEnd = body.indexOf("\"", indexStart);
                String songUrl = body.substring(indexStart, indexEnd).replace("\\/", "/");

                // Intenta obtener el título si está disponible
                String title = "Untitled";
                int tStart = body.indexOf("\"title\":\"");
                if (tStart != -1) {
                    int tEnd = body.indexOf("\"", tStart + 9);
                    title = body.substring(tStart + 9, tEnd);
                }

                return new GeneratedSongResult(title, songUrl);
            }
        }

        throw new RuntimeException("⏳ Tiempo agotado.");
    }

    /**
     * Envia una solicitud de generación musical al modelo alternativo "Qubico/diffrhythm".
     *
     * @param gptPrompt Prompt de estilo musical.
     * @return Resultado con título y URL.
     */
    private GeneratedSongResult submitWithDiffRhythm(String gptPrompt) throws IOException, InterruptedException {
        String requestBody = """
            {
              "model": "Qubico/diffrhythm",
              "task_type": "txt2audio-base",
              "input": {
                "lyrics": "AI-generated lyrics from prompt: %s",
                "style_audio": "",
                "style_prompt": "%s"
              },
              "config": {
                "service_mode": "public",
                "webhook_config": {
                  "endpoint": "",
                  "secret": ""
                }
              }
            }
            """.formatted(gptPrompt, gptPrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUBMIT_URL))
                .header("X-API-Key", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("❌ Error en submitWithDiffRhythm: " + response.body());
        }

        String body = response.body();
        int taskStart = body.indexOf("\"task_id\":\"") + 11;
        int taskEnd = body.indexOf("\"", taskStart);
        String taskId = body.substring(taskStart, taskEnd);

        return pollDiffRhythmResult(taskId);
    }

    /**
     * Realiza un polling periódico para obtener la canción generada por el modelo "Qubico/diffrhythm".
     *
     * @param taskId ID de la tarea enviada.
     * @return Resultado con título genérico y URL del audio.
     */
    private GeneratedSongResult pollDiffRhythmResult(String taskId) throws IOException, InterruptedException {
        String url = GET_URL_BASE + taskId;

        for (int i = 0; i < 30; i++) {
            Thread.sleep(10000);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-API-Key", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (body.contains("\"status\":\"completed\"") && body.contains("\"audio_url\"")) {
                int indexStart = body.indexOf("\"audio_url\":\"") + 13;
                int indexEnd = body.indexOf("\"", indexStart);
                String audioUrl = body.substring(indexStart, indexEnd).replace("\\/", "/");

                return new GeneratedSongResult("AI Track", audioUrl);
            }

            if (body.contains("\"status\":\"failed\"")) {
                throw new RuntimeException("❌ Falló DiffRhythm.");
            }
        }

        throw new RuntimeException("⏳ DiffRhythm no devolvió resultados a tiempo.");
    }
}
