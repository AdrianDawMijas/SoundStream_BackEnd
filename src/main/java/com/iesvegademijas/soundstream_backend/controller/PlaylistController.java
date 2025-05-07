package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.Playlist;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.PlaylistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Playlists", description = "Gestión de playlists y bibliotecas de usuarios")
@RestController
@RequestMapping("/v1/api/library")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    /**
     * 🔹 Obtiene la biblioteca de un usuario.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Playlist> getUserLibrary(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(playlistService.getUserLibrary(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🔹 Crea una biblioteca para un usuario si no la tiene.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Playlist> createUserLibrary(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.createUserLibrary(userId));
    }

    /**
     * 🔹 Agrega una canción a la biblioteca del usuario.
     */
    @PostMapping("/{userId}/add-song")
    public ResponseEntity<Playlist> addSongToLibrary(@PathVariable Long userId, @Valid @RequestBody Song song) {
        return ResponseEntity.ok(playlistService.addSongToLibrary(userId, song));
    }

    /**
     * 🔹 Elimina una canción de la biblioteca del usuario.
     */
    @DeleteMapping("/{userId}/remove-song/{songId}")
    public ResponseEntity<Playlist> removeSongFromLibrary(@PathVariable Long userId, @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.removeSongFromLibrary(userId, songId));
    }

    /**
     * 🔹 Vacía toda la biblioteca del usuario.
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearLibrary(@PathVariable Long userId) {
        try {
            playlistService.clearLibrary(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🔹 Manejo de excepciones global para errores.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
