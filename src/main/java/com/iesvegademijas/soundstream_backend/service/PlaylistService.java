package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Playlist;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.repository.PlaylistRepository;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserService userService; // 🔥 Ahora usamos el servicio en vez del repositorio

    @Autowired
    private SongRepository songRepository;

    /**
     * Obtiene la biblioteca de un usuario (su única playlist)
     */
    public Playlist getUserLibrary(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getLibrary();
    }

    /**
     * Crea una biblioteca (playlist) para un usuario si aún no la tiene
     */
    public Playlist createUserLibrary(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getLibrary() != null) {
            return user.getLibrary(); // Ya tiene una biblioteca
        }

        Playlist library = new Playlist();
        library.setName("Mi Biblioteca");
        library.setUser(user);
        library.setSongs(List.of()); // Inicialmente vacía
        library.setCreatedAt(LocalDateTime.now());

        playlistRepository.save(library);
        user.setLibrary(library);
        return playlistRepository.save(library);
    }

    /**
     * Agrega una canción a la biblioteca del usuario
     */
    public Playlist addSongToLibrary(Long userId, Song song) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Playlist library = user.getLibrary();
        if (library == null) {
            library = createUserLibrary(userId); // Si no tiene, se la creamos
        }

        song.setPlaylist(library);
        songRepository.save(song);
        library.getSongs().add(song);
        return playlistRepository.save(library);
    }

    /**
     * Elimina una canción de la biblioteca del usuario
     */
    public Playlist removeSongFromLibrary(Long userId, Long songId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Playlist library = user.getLibrary();
        if (library == null) {
            throw new RuntimeException("El usuario no tiene una biblioteca");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        if (!library.getSongs().contains(song)) {
            throw new RuntimeException("La canción no pertenece a la biblioteca del usuario");
        }

        library.getSongs().remove(song);
        songRepository.delete(song);
        return playlistRepository.save(library);
    }

    /**
     * Vacía toda la biblioteca del usuario
     */
    public void clearLibrary(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Playlist library = user.getLibrary();
        if (library == null) {
            throw new RuntimeException("El usuario no tiene una biblioteca");
        }

        songRepository.deleteAll(library.getSongs());
        library.getSongs().clear();
        playlistRepository.save(library);
    }
}
