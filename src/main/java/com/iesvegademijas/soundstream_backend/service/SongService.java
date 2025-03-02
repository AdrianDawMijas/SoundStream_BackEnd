package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public List<Song> getSongsByUser(Long userId) {
        return songRepository.findByUserId(userId);
    }

    public Song saveSong(Song song) {
        song.setCreatedAt(LocalDateTime.now());
        return songRepository.save(song);
    }

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
}
