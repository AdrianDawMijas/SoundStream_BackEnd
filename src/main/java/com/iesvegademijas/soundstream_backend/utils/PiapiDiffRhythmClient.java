package com.iesvegademijas.soundstream_backend.utils;

import jakarta.validation.constraints.Pattern;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;

@Component
public class PiapiDiffRhythmClient {

    private static final String API_KEY = "416c8ff7b948328707a0e24a85f98f0a8b28a7ceab6c5d5455f81fb4339d9e4e";
    private static final String SUBMIT_URL = "https://api.piapi.ai/api/v1/task";
    private static final String GET_URL_BASE = "https://api.piapi.ai/api/v1/task/";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String submitGenerationTask(String gptPrompt) throws IOException, InterruptedException {
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

        String body = response.body();
        String taskId = null;

        if (response.statusCode() == 200) {
            int index = body.indexOf("\"task_id\":\"");
            if (index != -1) {
                int start = index + 11;
                int end = body.indexOf("\"", start);
                taskId = body.substring(start, end);
            }

            if (taskId == null || taskId.isBlank()) {
                throw new RuntimeException("❌ Error: no se pudo extraer task_id. Respuesta completa: " + body);
            }

            System.out.println("✅ Task ID obtenido: " + taskId);
            return taskId;
        } else {
            throw new RuntimeException("❌ Error HTTP: " + response.statusCode() + ". Respuesta: " + body);
        }
    }

    public GeneratedSongResult pollForAudioUrl(String taskId) throws IOException, InterruptedException {
        String url = GET_URL_BASE + taskId;

        for (int i = 0; i < 20; i++) {
            Thread.sleep(12000); // Espera 6s entre cada intento

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-API-Key", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            System.out.println("🔁 Polling intento " + (i + 1));
            System.out.println("📄 Respuesta: " + body);

            if (body.contains("\"status\":\"failed\"")) {
                throw new RuntimeException("❌ La generación falló.");
            }

            if (body.contains("\"status\":\"completed\"") && body.contains("\"song_path\"")) {
                // Extraer URL
                int indexStart = body.indexOf("\"song_path\":\"");
                if (indexStart != -1) {
                    indexStart += "\"song_path\":\"".length();
                    int indexEnd = body.indexOf("\"", indexStart);
                    if (indexEnd != -1) {
                        String urlAudio = body.substring(indexStart, indexEnd).replace("\\/", "/");

                        // Extraer título
                        String title = "Untitled";
                        int titleStart = body.indexOf("\"title\":\"");
                        if (titleStart != -1) {
                            titleStart += "\"title\":\"".length();
                            int titleEnd = body.indexOf("\"", titleStart);
                            if (titleEnd != -1) {
                                title = body.substring(titleStart, titleEnd);
                            }
                        }

                        System.out.println("✅ Título: " + title);
                        System.out.println("✅ URL encontrada: " + urlAudio);
                        return new GeneratedSongResult(title, urlAudio);
                    }
                }
            }
        }

        throw new RuntimeException("⏳ Tiempo agotado. No se obtuvo una canción generada.");
    }

}
