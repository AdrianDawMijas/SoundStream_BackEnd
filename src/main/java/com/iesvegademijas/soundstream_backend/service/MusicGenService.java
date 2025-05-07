package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.config.SongStorageProperties;
import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import com.iesvegademijas.soundstream_backend.utils.AudioTrimmerService;
import com.iesvegademijas.soundstream_backend.utils.GeneratedSongResult;
import com.iesvegademijas.soundstream_backend.utils.PiapiDiffRhythmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class MusicGenService {

    private final PiapiDiffRhythmClient sunoApiClient;
    private final SongRepository songRepository;
    private final SongService songService;

    @Autowired
    private SongStorageProperties songStorageProperties;

    @Autowired
    public MusicGenService(PiapiDiffRhythmClient sunoApiClient, SongRepository songRepository, SongService songService) {
        this.sunoApiClient = sunoApiClient;
        this.songRepository = songRepository;
        this.songService = songService;
    }

    /**
     * 🔹 Genera música usando PiAPI y guarda la canción.
     */
    public Song generateAndSaveMusic(SongDTO songDTO) throws Exception {
        String prompt = songDTO.getPromptText();

        if (prompt == null || prompt.isBlank()) {
            prompt = "A " + songDTO.getGenre() + " track"
                    + (songDTO.getSubgenre() != null ? " in the style of " + songDTO.getSubgenre() : "")
                    + (songDTO.getInstrumentNames() != null && !songDTO.getInstrumentNames().isEmpty()
                    ? " using " + String.join(", ", songDTO.getInstrumentNames()) : "");
        }

        String taskId = sunoApiClient.submitGenerationTask(prompt);
        GeneratedSongResult result = waitForGeneratedSong(taskId);

        if (result == null || result.getUrl() == null) {
            throw new RuntimeException("No se pudo generar la canción (audio_url es null)");
        }

        Song song = songService.createSongFromDTO(songDTO);

        if(songDTO.getDuration() != null && songDTO.getDuration() > 0) {
            File trimmed = AudioTrimmerService.trimAudio(result.getUrl(), songDTO.getDuration());
            Path outputPath = Paths.get(songStorageProperties.getStoragePath(), trimmed.getName());
            Files.copy(trimmed.toPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("🔍 Tamaño del archivo recortado: " + trimmed.length());
            Thread.sleep(500);
            String finalUrl = songStorageProperties.getBaseUrl() + "/" + trimmed.getName();
            System.out.println("🎧 URL generada para audio: " + finalUrl);
            song.setGeneratedUrl(finalUrl);
        }
        else {
            song.setGeneratedUrl(result.getUrl());
            System.out.println("URL generada para audio: " + result.getUrl() );
        }


        // Si quieres sobrescribir el título, solo si viene uno generado:
        if (result.getTitle() != null && !result.getTitle().isBlank()) {
            song.setTitle(result.getTitle());
        }

        return songRepository.save(song);
    }

    private GeneratedSongResult waitForGeneratedSong(String taskId) throws Exception {
        int maxRetries = 10;
        int delayMillis = 3000;

        for (int i = 0; i < maxRetries; i++) {
            GeneratedSongResult result = sunoApiClient.pollForAudioUrl(taskId);
            if (result != null && result.getUrl() != null) {
                return result;
            }
            Thread.sleep(delayMillis);
        }
        return null;
    }

}
