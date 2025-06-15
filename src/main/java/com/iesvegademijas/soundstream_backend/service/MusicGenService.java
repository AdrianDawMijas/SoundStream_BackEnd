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
import java.nio.file.*;

@Service
public class MusicGenService {

    private final PiapiDiffRhythmClient sunoApiClient;
    private final SongRepository songRepository;
    private final SongService songService;

    @Autowired
    private SongStorageProperties songStorageProperties;

    @Autowired
    public MusicGenService(PiapiDiffRhythmClient sunoApiClient,
                           SongRepository songRepository,
                           SongService songService) {
        this.sunoApiClient = sunoApiClient;
        this.songRepository = songRepository;
        this.songService = songService;
    }

    /**
     * Genera música mediante la IA y guarda la canción en el sistema.
     */
    public Song generateAndSaveMusic(SongDTO songDTO) throws Exception {
        String prompt = songDTO.getPromptText();

        if (prompt == null || prompt.isBlank()) {
            prompt = "A " + songDTO.getGenre() + " track";

            if (songDTO.getSubgenre() != null && !songDTO.getSubgenre().isBlank()) {
                prompt += " in the style of " + songDTO.getSubgenre();
            }
        }


        GeneratedSongResult result = sunoApiClient.submitSongGeneration(prompt);
        if (result == null || result.getUrl() == null) {
            throw new RuntimeException("No se pudo generar la canción (audio_url es null)");
        }

        Song song = songService.saveSong(songDTO);

        if (songDTO.getDuration() != null && songDTO.getDuration() > 0) {
            File trimmed = AudioTrimmerService.trimAudio(result.getUrl(), songDTO.getDuration());
            Path outputPath = Paths.get(songStorageProperties.getStoragePath(), trimmed.getName());
            Files.copy(trimmed.toPath(), outputPath, StandardCopyOption.REPLACE_EXISTING);
            String finalUrl = songStorageProperties.getBaseUrl() + "/" + trimmed.getName();
            song.setGeneratedUrl(finalUrl);
        } else {
            song.setGeneratedUrl(result.getUrl());
        }

        if (result.getTitle() != null && !result.getTitle().isBlank()) {
            song.setTitle(result.getTitle());
        }

        return songRepository.save(song);
    }
}
