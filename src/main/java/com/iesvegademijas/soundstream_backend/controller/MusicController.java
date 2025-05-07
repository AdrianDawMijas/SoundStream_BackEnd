package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.MusicGenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.io.IOException;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/songs")
public class MusicController {

    private final MusicGenService musicGenService;

    public MusicController(MusicGenService musicGenService) {
        this.musicGenService = musicGenService;
    }

    /**
     * 🎵 Genera una canción basada en los parámetros del usuario
     * @param songDTO Datos de la canción a generar
     * @return La canción generada o un mensaje de error si falla la generación
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateMusic(@Valid @RequestBody SongDTO songDTO) {
        try {
            // 🔹 Generamos la canción
            Song song = musicGenService.generateAndSaveMusic(songDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(song);

        } catch (IOException e) {
            // ❌ Manejo de errores si hay problemas con la API externa
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generando la canción: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
