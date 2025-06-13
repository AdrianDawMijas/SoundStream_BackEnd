package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.*;
import com.iesvegademijas.soundstream_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public List<Song> getSongsByUserId(Long userId) {
        return songRepository.findByUser_Id(userId);
    }

    public Song saveSong(SongDTO songDTO) {
        Song song = createSongFromDTO(songDTO);
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

        if (songDTO.getGenre() != null && !songDTO.getGenre().isBlank()) {
            Genre genre = getOrCreateGenre(songDTO.getGenre());
            song.setGenre(genre);

            if (songDTO.getSubgenre() != null && !songDTO.getSubgenre().isBlank()) {
                Subgenre subgenre = getOrCreateSubgenre(songDTO.getSubgenre(), genre);
                song.setSubgenre(subgenre);
            }
        }

        List<String> instrumentNames = Optional.ofNullable(songDTO.getInstrumentNames()).orElse(new ArrayList<>());
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

    private List<Instrument> getOrCreateInstruments(List<String> names) {
        if (names.isEmpty()) return new ArrayList<>();
        List<Instrument> existing = instrumentRepository.findByNameIn(names);
        return names.stream()
                .map(name -> existing.stream()
                        .filter(i -> i.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseGet(() -> instrumentRepository.save(new Instrument(name))))
                .toList();
    }

    public List<Song> getSongsByFilters(Long userId, Long genreId, Long subgenreId,
                                        Double minDuration, Double maxDuration, List<Long> instrumentIds) {
        return songRepository.findByFilters(
                userId, genreId, subgenreId, minDuration, maxDuration,
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
        return songRepository.findAll();
    }

    public Optional<Song> getRandomSong() {
        List<Song> all = songRepository.findAll();
        return all.isEmpty() ? Optional.empty()
                : Optional.of(all.get(new Random().nextInt(all.size())));
    }

    public boolean assignUserToSong(Long songId, Long userId) {
        Optional<Song> optSong = songRepository.findById(songId);
        Optional<User> optUser = userRepository.findById(userId);

        if (optSong.isPresent() && optUser.isPresent()) {
            Song song = optSong.get();
            song.setUser(optUser.get());
            songRepository.save(song);
            return true;
        }
        return false;
    }
}
