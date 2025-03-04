package com.iesvegademijas.soundstream_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Genre;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.model.Subgenre;
import com.iesvegademijas.soundstream_backend.repository.GenreRepository;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import com.iesvegademijas.soundstream_backend.repository.SubgenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@Service
public class MusicGenService {

    @Value("${huggingface.api.key}")
    private String API_KEY;

    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/musicgen-small";

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private SubgenreRepository subgenreRepository;

    public Song generateAndSaveMusic(SongDTO songDTO) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        // 🔹 Validar que el género no sea null
        if (songDTO.getGenre() == null || songDTO.getGenre().trim().isEmpty()) {
            throw new RuntimeException("Error: El género no puede ser nulo o vacío.");
        }

        // 🔹 Buscar o crear el género
        Genre genre = genreRepository.findByNameIgnoreCase(songDTO.getGenre())
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(songDTO.getGenre());
                    return genreRepository.save(newGenre);  // Guardamos y retornamos
                });

        // 🔹 Buscar o crear el subgénero (si existe)
        Subgenre subgenre = null;
        if (songDTO.getSubgenre() != null && !songDTO.getSubgenre().isEmpty()) {
            subgenre = subgenreRepository.findByNameIgnoreCase(songDTO.getSubgenre())
                    .orElseGet(() -> {
                        Subgenre newSubgenre = new Subgenre();
                        newSubgenre.setName(songDTO.getSubgenre());
                        newSubgenre.setGenre(genre);
                        return subgenreRepository.save(newSubgenre);
                    });
        }

        // 🔹 Construcción del prompt
        String prompt = "Generate a " + genre.getName() + " song with " + songDTO.getInstruments() +
                (subgenre != null ? ", subgenre: " + subgenre.getName() : "") +
                ", tempo: " + songDTO.getTempo() + " BPM, duration: " + songDTO.getDuration() + " minutes.";

        String requestBody = "{\"inputs\": \"" + prompt + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // 🔹 Realizar solicitud a la API
        ResponseEntity<byte[]> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, byte[].class);

        // 🔹 Verificar Content-Type de la respuesta
        MediaType contentType = response.getHeaders().getContentType();
        byte[] responseBody = response.getBody();

        // 🔹 Crear una nueva canción con los datos base
        Song song = new Song();
        song.setGenre(genre);
        song.setSubgenre(subgenre);
        song.setDuration(songDTO.getDuration());
        song.setTempo(songDTO.getTempo());
        song.setInstruments(songDTO.getInstruments());
        song.setCreatedAt(LocalDateTime.now());

        if (contentType != null && contentType.toString().startsWith("audio/")) {
            // ✅ La API devolvió un archivo de audio
            String fileExtension = contentType.getSubtype().equals("flac") ? "flac" : "mp3";
            Path directoryPath = Paths.get("generated_audio");

            // ✅ Crear el directorio si no existe
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // ✅ Guardar el archivo sin sobreescribir
            Path filePath;
            do {
                filePath = directoryPath.resolve(UUID.randomUUID() + "." + fileExtension);
            } while (Files.exists(filePath)); // Asegurarse de que no se sobreescriba un archivo existente

            Files.write(filePath, responseBody);

            // 🔹 Guardar la URL local en la base de datos
            song.setGeneratedUrl(filePath.toString());

        } else {
            // ❌ La API devolvió JSON en lugar de audio, procesamos la respuesta
            try {
                String jsonResponse = new String(responseBody);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonResponse);

                if (jsonNode.has("audio_url")) {
                    String generatedUrl = jsonNode.get("audio_url").asText();
                    song.setGeneratedUrl(generatedUrl);
                } else {
                    throw new RuntimeException("No se encontró una URL de audio en la respuesta.");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al procesar el JSON de la API de MusicGen", e);
            }
        }

        // 🔹 Guardar la canción con todos los datos asignados
        return songRepository.save(song);
    }
}
