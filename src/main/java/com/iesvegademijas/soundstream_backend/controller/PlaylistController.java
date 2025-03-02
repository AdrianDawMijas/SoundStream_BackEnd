package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.Playlist;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Playlists", description = "Gestión de playlists y bibliotecas de usuarios")
@RestController
@RequestMapping("/v1/api/library")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    // Obtener la biblioteca del usuario
    @GetMapping("/{userId}")
    public ResponseEntity<Playlist> getUserLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getUserLibrary(userId));
    }

    // Crear una biblioteca para un usuario (si no la tiene)
    @PostMapping("/{userId}")
    public ResponseEntity<Playlist> createUserLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.createUserLibrary(userId));
    }

    // Agregar una canción a la biblioteca del usuario
    @PostMapping("/{userId}/add-song")
    public ResponseEntity<Playlist> addSongToLibrary(@PathVariable Long userId, @RequestBody Song song) {
        return ResponseEntity.ok(playlistService.addSongToLibrary(userId, song));
    }

    // Eliminar una canción de la biblioteca del usuario
    @DeleteMapping("/{userId}/remove-song/{songId}")
    public ResponseEntity<Playlist> removeSongFromLibrary(@PathVariable Long userId, @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.removeSongFromLibrary(userId, songId));
    }

    // Vaciar toda la biblioteca del usuario
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearLibrary(@PathVariable Long userId) {
        playlistService.clearLibrary(userId);
        return ResponseEntity.noContent().build();
    }
}
