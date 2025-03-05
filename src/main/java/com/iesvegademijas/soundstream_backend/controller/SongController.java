package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;

@Tag(name = "Canciones", description = "Gestión de canciones generadas por IA")
@Slf4j
@RestController
@RequestMapping("/v1/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    // 🔹 Obtener todas las canciones de una playlist con filtros dinámicos
    @GetMapping("/playlist/{playlistId}/filter")
    public ResponseEntity<List<Song>> getSongsByFilters(
            @PathVariable Long playlistId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long subgenreId,
            @RequestParam(required = false) Double minDuration,
            @RequestParam(required = false) Double maxDuration,
            @RequestParam(required = false) List<Long> instrumentIds) {

        log.info("Filtrando canciones en playlist {} con parámetros: género={}, subgénero={}, duraciónminina={}, duracionmaxima={}, instrumentos={}",
                playlistId, genreId, subgenreId, minDuration, maxDuration, instrumentIds);

        List<Song> songs = songService.getSongsByFilters(playlistId, genreId, subgenreId, minDuration, maxDuration, instrumentIds);
        return ResponseEntity.ok(songs);
    }

    // 🔹 Obtener una canción por ID con manejo de errores
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        return song.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("⚠️ Canción no encontrada con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // 🔹 Eliminar una canción con logs y validación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        log.info("🗑 Eliminando canción con ID: {}", id);

        boolean deleted = songService.deleteSong(id);
        if (!deleted) {
            log.warn("⚠️ No se pudo eliminar la canción con ID: {} porque no existe.", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
