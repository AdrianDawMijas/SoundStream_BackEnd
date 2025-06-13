package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.dto.SongAddDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para operaciones CRUD y filtros sobre canciones.
 */
@Tag(name = "Canciones", description = "Gestión de canciones generadas por IA")
@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Song>> getSongsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(songService.getSongsByUserId(userId));
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<Song>> getSongsByFilters(
            @PathVariable Long userId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long subgenreId,
            @RequestParam(required = false) Double minDuration,
            @RequestParam(required = false) Double maxDuration,
            @RequestParam(required = false) List<Long> instrumentIds
    ) {
        return ResponseEntity.ok(
                songService.getSongsByFilters(userId, genreId, subgenreId, minDuration, maxDuration, instrumentIds)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        return songService.deleteSong(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/random")
    public ResponseEntity<Song> getRandomSong() {
        return songService.getRandomSong()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<Void> addSongToUser(@PathVariable Long userId, @RequestBody SongAddDTO dto) {
        boolean success = songService.assignUserToSong(dto.getSongId(), userId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
