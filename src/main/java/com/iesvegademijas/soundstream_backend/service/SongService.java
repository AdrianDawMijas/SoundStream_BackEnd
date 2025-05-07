package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.*;
import com.iesvegademijas.soundstream_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SubgenreRepository subgenreRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserRepository userRepository;

    @Autowired
    public SongService(SongRepository songRepository,
                       GenreRepository genreRepository,
                       InstrumentRepository instrumentRepository,
                       SubgenreRepository subgenreRepository,
                       UserRepository userRepository) {
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
        this.subgenreRepository = subgenreRepository;
        this.instrumentRepository = instrumentRepository;
        this.userRepository = userRepository;
    }

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public Song saveSong(SongDTO songDTO) {
        Song song = createSongFromDTO(songDTO);

        // 🔹 Buscar al usuario
        User user = userRepository.findById(songDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        song.setUser(user);

        return songRepository.save(song);
    }

    public Song createSongFromDTO(SongDTO songDTO) {
        Song song = new Song();
        song.setDuration(songDTO.getDuration());
        song.setTempo(songDTO.getTempo());
        song.setPromptText(songDTO.getPromptText());
        song.setCreatedAt(LocalDateTime.now());

        // 🔹 Crear género si se proporciona
        if (songDTO.getGenre() != null && !songDTO.getGenre().isBlank()) {
            Genre genre = getOrCreateGenre(songDTO.getGenre());
            song.setGenre(genre);

            // 🔹 Crear subgénero solo si también hay género
            if (songDTO.getSubgenre() != null && !songDTO.getSubgenre().isBlank()) {
                Subgenre subgenre = getOrCreateSubgenre(songDTO.getSubgenre(), genre);
                song.setSubgenre(subgenre);
            }
        }

        // 🔹 Instrumentos
        List<String> instrumentNames = songDTO.getInstrumentNames() != null
                ? songDTO.getInstrumentNames()
                : new ArrayList<>();
        song.setInstruments(getOrCreateInstruments(instrumentNames));
        song.setTitle(songDTO.getTitle());

        return song;
    }

    private Genre getOrCreateGenre(String genreName) {
        return genreRepository.findByNameIgnoreCase(genreName)
                .orElseGet(() -> genreRepository.save(new Genre(genreName)));
    }

    private Subgenre getOrCreateSubgenre(String subgenreName, Genre genre) {
        return subgenreRepository.findByNameIgnoreCase(subgenreName)
                .orElseGet(() -> subgenreRepository.save(new Subgenre(subgenreName, genre)));
    }

    private List<Instrument> getOrCreateInstruments(List<String> instrumentNames) {
        if (instrumentNames.isEmpty()) return new ArrayList<>();

        List<Instrument> existing = instrumentRepository.findByNameIn(instrumentNames);

        return new ArrayList<>(instrumentNames.stream()
                .map(name -> existing.stream()
                        .filter(i -> i.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseGet(() -> instrumentRepository.save(new Instrument(name))))
                .toList());
    }

    public List<Song> getSongsByFilters(Long playlistId, Long genreId, Long subgenreId,
                                        Double minDuration, Double maxDuration, List<Long> instrumentIds) {
        return songRepository.findByFilters(
                playlistId,
                genreId,
                subgenreId,
                minDuration,
                maxDuration,
                (instrumentIds != null && !instrumentIds.isEmpty()) ? instrumentIds : null
        );
    }

    public boolean deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new RuntimeException("La canción con ID " + id + " no existe.");
        }
        songRepository.deleteById(id);
        return true;
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll(); // Asegúrate de importar tu repositorio correctamente
    }

}
