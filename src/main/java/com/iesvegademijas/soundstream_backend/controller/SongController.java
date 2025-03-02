package com.iesvegademijas.soundstream_backend.controller;


import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Canciones", description = "Gestión de canciones generadas por IA")
@Slf4j
@RestController
@RequestMapping("/v1/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    // Obtener todas las canciones
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // Obtener todas las canciones de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Song>> getSongsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(songService.getSongsByUser(userId));
    }

    // Obtener una canción por ID con manejo de errores
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        return song.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Canción no encontrada con ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Guardar una nueva canción con logs
    @PostMapping
    public ResponseEntity<Song> saveSong(@RequestBody Song song) {
        log.info("Guardando nueva canción: " + song);
        return ResponseEntity.status(201).body(songService.saveSong(song));
    }

    // Eliminar una canción con logs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        log.info("Eliminando canción con ID: " + id);
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
