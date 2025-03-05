package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.*;
import com.iesvegademijas.soundstream_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SubgenreRepository subgenreRepository;
    private final InstrumentRepository instrumentRepository;
    private final UserRepository userRepository; // 🆕 Repositorio de usuarios

    @Autowired
    public SongService(SongRepository songRepository, GenreRepository genreRepository,
                       InstrumentRepository instrumentRepository, SubgenreRepository subgenreRepository,
                       UserRepository userRepository) { // 🆕 Pasamos el UserRepository
        this.songRepository = songRepository;
        this.genreRepository = genreRepository;
        this.subgenreRepository = subgenreRepository;
        this.instrumentRepository = instrumentRepository;
        this.userRepository = userRepository; // 🆕 Guardamos la referencia
    }

    // 🔹 Obtener una canción por ID
    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }


    // 🔹 Guardar una canción con usuario, género, subgénero e instrumentos
    public Song saveSong(SongDTO songDTO) {
        Song song = new Song();
        song.setDuration(songDTO.getDuration());
        song.setTempo(songDTO.getTempo());
        song.setPromptText(songDTO.getPromptText());
        song.setCreatedAt(java.time.LocalDateTime.now());

        // 🔹 Buscar al usuario en la BD (si no existe, lanza error)
        User user = userRepository.findById(songDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        song.setUser(user); // ✅ Asignamos el usuario

        // 🔹 Buscar o crear el género
        song.setGenre(getOrCreateGenre(songDTO.getGenre()));

        // 🔹 Buscar o crear el subgénero (si existe)
        song.setSubgenre(getOrCreateSubgenre(songDTO.getSubgenre(), song.getGenre()));

        // 🔹 Convertir los instrumentos de String a Lista de entidades
        song.setInstruments(getOrCreateInstruments(songDTO.getInstrumentNames()));

        return songRepository.save(song);
    }

    // 🔹 Método helper: Buscar o crear un género
    private Genre getOrCreateGenre(String genreName) {
        return genreRepository.findByNameIgnoreCase(genreName)
                .orElseGet(() -> genreRepository.save(new Genre(genreName)));
    }

    // 🔹 Método helper: Buscar o crear un subgénero
    private Subgenre getOrCreateSubgenre(String subgenreName, Genre genre) {
        if (subgenreName == null || subgenreName.isEmpty()) return null;
        return subgenreRepository.findByNameIgnoreCase(subgenreName)
                .orElseGet(() -> subgenreRepository.save(new Subgenre(subgenreName, genre)));
    }

    // 🔹 Método helper: Convertir los nombres de instrumentos en entidades
    private List<Instrument> getOrCreateInstruments(List<String> instrumentNames) {
        if (instrumentNames == null || instrumentNames.isEmpty()) return new ArrayList<>(); // ✅ Retornamos lista mutable

        List<Instrument> existingInstruments = instrumentRepository.findByNameIn(instrumentNames);

        return new ArrayList<>(instrumentNames.stream()
                .map(name -> existingInstruments.stream()
                        .filter(i -> i.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseGet(() -> instrumentRepository.save(new Instrument(name))))
                .toList()); // ✅ Convertimos a lista mutable
    }


    // 🔹 Método helper: Crear una entidad Song desde un SongDTO
    public Song createSongFromDTO(SongDTO songDTO) {
        Song song = new Song();
        song.setDuration(songDTO.getDuration());
        song.setTempo(songDTO.getTempo());
        song.setPromptText(songDTO.getPromptText());
        song.setCreatedAt(java.time.LocalDateTime.now());

        // 🔹 Buscar o crear el género
        song.setGenre(getOrCreateGenre(songDTO.getGenre()));

        // 🔹 Buscar o crear el subgénero (si existe)
        song.setSubgenre(getOrCreateSubgenre(songDTO.getSubgenre(), song.getGenre()));

        // 🔹 Convertir los instrumentos de String a Lista de entidades
        song.setInstruments(getOrCreateInstruments(songDTO.getInstrumentNames()));

        return song;
    }

    // 🔹 Filtrar por múltiples criterios dentro de una playlist
    public List<Song> getSongsByFilters(Long playlistId, Long genreId, Long subgenreId, Double minDuration,
                                        Double maxDuration, List<Long> instrumentIds) {
        return songRepository.findByFilters(playlistId, genreId, subgenreId, minDuration, maxDuration,
                (instrumentIds != null && !instrumentIds.isEmpty()) ? instrumentIds : null);
    }

    // 🔹 Eliminar una canción con verificación
    public boolean deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new RuntimeException("La canción con ID " + id + " no existe.");
        }
        songRepository.deleteById(id);
        return true;
    }

}
