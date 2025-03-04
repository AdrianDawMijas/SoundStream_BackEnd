package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.dto.SongDTO;
import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.service.MusicGenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/music")
public class MusicController {
    private final MusicGenService musicGenService;

    public MusicController(MusicGenService musicGenService) {
        this.musicGenService = musicGenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Song> generateMusic(@RequestBody SongDTO songDTO) throws IOException {
        Song song = musicGenService.generateAndSaveMusic(songDTO);
        return ResponseEntity.status(201).body(song);
    }
}

