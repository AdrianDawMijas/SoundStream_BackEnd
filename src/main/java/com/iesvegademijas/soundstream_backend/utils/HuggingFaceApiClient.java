package com.iesvegademijas.soundstream_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class HuggingFaceApiClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/musicgen-small";
    private static final String AUDIO_MEDIA_TYPE = "audio/flac";

    @Value("${huggingface.api.key}")
    private String API_KEY;

    public HuggingFaceApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.API_KEY = System.getenv("HUGGINGFACE_API_KEY"); // O usa @Value("${huggingface.api.key}")
    }

    /**
     * 🔹 Llama a la API de Hugging Face y obtiene la respuesta en bytes.
     */
    public byte[] callMusicGenAPI(String prompt) {
        return getBytes(prompt, API_KEY, restTemplate, API_URL);
    }

    public static byte[] getBytes(String prompt, String apiKey, RestTemplate restTemplate, String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        String requestBody = "{\"inputs\": \"" + prompt + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, byte[].class);
        return response.getBody();
    }

    /**
     * 🔹 Obtiene el tipo de contenido de la respuesta de la API.
     */
    public MediaType getContentType(byte[] responseBody) {
        try {
            String jsonResponse = new String(responseBody);
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            if (jsonNode.has("audio_url")) {
                return MediaType.APPLICATION_JSON;
            }
        } catch (IOException e) {
            // Si hay un error al parsear JSON, asumimos que es un archivo de audio
            return MediaType.parseMediaType(AUDIO_MEDIA_TYPE);
        }
        return null;
    }
}
