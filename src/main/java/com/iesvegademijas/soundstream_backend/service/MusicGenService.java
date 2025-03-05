package com.iesvegademijas.soundstream_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import com.iesvegademijas.soundstream_backend.utils.HuggingFaceApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MusicGenService {

    private final HuggingFaceApiClient huggingFaceApiClient;
    private final SongRepository songRepository;
    private final SongService songService;  // 🔥 Usamos SongService para evitar duplicación

    @Autowired
    public MusicGenService(HuggingFaceApiClient huggingFaceApiClient, SongRepository songRepository, SongService songService) {
        this.huggingFaceApiClient = huggingFaceApiClient;
        this.songRepository = songRepository;
        this.songService = songService;
    }


    /**
     * 🔹 Genera música usando Hugging Face y guarda la canción en la base de datos.
     */
    public Song generateAndSaveMusic(SongDTO songDTO) throws IOException {
        // 🔹 Construimos el prompt
        String prompt = buildPrompt(songDTO);

        // 🔹 Llamamos a la API y obtenemos los bytes de la respuesta
        byte[] responseBody = huggingFaceApiClient.callMusicGenAPI(prompt);

        // 🔹 Obtenemos el tipo de contenido de la respuesta
        MediaType contentType = huggingFaceApiClient.getContentType(responseBody);

        // 🔹 Creamos la canción usando SongService para asignar género, subgénero e instrumentos
        Song song = songService.createSongFromDTO(songDTO);

        // 🔹 Procesamos la respuesta de la API
        processApiResponse(responseBody, contentType, song);

        // 🔹 Guardamos la canción en la base de datos
        return songRepository.save(song);
    }

    /**
     * 🔹 Construye un prompt basado en el DTO.
     */
    private String buildPrompt(SongDTO songDTO) {
        if (songDTO.getPromptText() != null && !songDTO.getPromptText().isEmpty()) {
            return songDTO.getPromptText(); // 🔹 Si el usuario proporciona un prompt, lo usamos
        }
        return "Generate a " + songDTO.getGenre() + " song with " + String.join(", ", songDTO.getInstrumentNames()) +
                ", tempo: " + songDTO.getTempo() + " BPM, duration: " + songDTO.getDuration() + " minutes.";
    }

    /**
     * 🔹 Maneja la respuesta de la API y guarda el audio si es necesario.
     */
    private void processApiResponse(byte[] responseBody, MediaType contentType, Song song) throws IOException {
        if (contentType.equals(MediaType.APPLICATION_JSON)) {
            // 🔹 Si la respuesta es JSON, obtenemos la URL del audio
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new String(responseBody));
            song.setGeneratedUrl(jsonNode.get("audio_url").asText());
        } else {
            // 🔹 Si la respuesta es audio, lo guardamos en el servidor
            Path filePath = Paths.get("generated_audio/" + UUID.randomUUID() + ".flac");
            Files.write(filePath, responseBody);
            song.setGeneratedUrl(filePath.toString());
        }
    }
}
