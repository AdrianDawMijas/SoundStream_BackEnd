package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Playlist;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.repository.PlaylistRepository;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserService userService; // ✅ Mejor encapsulación

    @Autowired
    private SongRepository songRepository;

    /**
     * 🔹 Encuentra un usuario por ID o lanza una excepción.
     */
    private User findUserById(Long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * 🔹 Obtiene la biblioteca del usuario.
     */
    public Playlist getUserLibrary(Long userId) {
        return findUserById(userId).getLibrary();
    }

    /**
     * 🔹 Crea una biblioteca para un usuario si aún no la tiene.
     */
    public Playlist createUserLibrary(Long userId) {
        User user = findUserById(userId);

        if (user.getLibrary() != null) {
            return user.getLibrary();
        }

        Playlist library = new Playlist();
        library.setUser(user);
        library.setSongs(new ArrayList<>()); // ✅ Lista mutable
        library.setCreatedAt(LocalDateTime.now());

        user.setLibrary(library);
        return playlistRepository.save(library);
    }

    /**
     * 🔹 Agrega una canción a la biblioteca del usuario.
     */
    public Playlist addSongToLibrary(Long userId, Song song) {
        User user = findUserById(userId);
        Playlist library = user.getLibrary();
        if (library == null) {
            library = createUserLibrary(userId);
        }

        song.setPlaylist(library);
        songRepository.save(song);
        return library;
    }

    /**
     * 🔹 Elimina una canción de la biblioteca del usuario.
     */
    public Playlist removeSongFromLibrary(Long userId, Long songId) {
        User user = findUserById(userId);
        Playlist library = user.getLibrary();
        if (library == null) throw new RuntimeException("El usuario no tiene una biblioteca");

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        if (!library.getSongs().contains(song)) {
            throw new RuntimeException("La canción no pertenece a la biblioteca del usuario");
        }

        songRepository.delete(song);
        return library;
    }

    /**
     * 🔹 Vacía toda la biblioteca del usuario.
     */
    public void clearLibrary(Long userId) {
        User user = findUserById(userId);
        Playlist library = user.getLibrary();
        if (library == null) throw new RuntimeException("El usuario no tiene una biblioteca");

        songRepository.deleteAllByPlaylist(library); // ✅ Método más eficiente
    }
}
