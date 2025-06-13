package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.MusicGenService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controlador para la generación de canciones mediante IA.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/songs")
public class MusicController {

    private final MusicGenService musicGenService;

    public MusicController(MusicGenService musicGenService) {
        this.musicGenService = musicGenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateMusic(@Valid @RequestBody SongDTO songDTO) {
        try {
            Song song = musicGenService.generateAndSaveMusic(songDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(song);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando la canción: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
