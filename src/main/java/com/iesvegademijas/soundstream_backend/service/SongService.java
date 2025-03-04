package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Genre;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.model.Subgenre;
import com.iesvegademijas.soundstream_backend.repository.GenreRepository;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import com.iesvegademijas.soundstream_backend.repository.SubgenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SubgenreRepository subgenreRepository;

    public SongService(SongRepository songRepository, GenreRepository genreRepository, SubgenreRepository subgenreRepository) {
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
        this.subgenreRepository = subgenreRepository;
    }

    // Obtener todas las canciones
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    // Obtener una canción por ID
    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    // Guardar una canción con género y subgénero
    public Song saveSong(Song song, Long genreId, Long subgenreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        song.setGenre(genre);

        if (subgenreId != null) {
            Subgenre subgenre = subgenreRepository.findById(subgenreId)
                    .orElseThrow(() -> new RuntimeException("Subgénero no encontrado"));
            song.setSubgenre(subgenre);
        }

        return songRepository.save(song);
    }

    // Eliminar una canción
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
}
