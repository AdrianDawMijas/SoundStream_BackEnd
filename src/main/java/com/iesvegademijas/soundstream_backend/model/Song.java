package com.iesvegademijas.soundstream_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double duration;     // Duración de la canción en segundos
    private Integer tempo;       // Tempo en BPM
    private String title;        // Título de la canción

    @ManyToMany
    @JoinTable(
            name = "song_instruments",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private List<Instrument> instruments;

    private String promptText;   // Texto usado para generar la canción

    @Column(columnDefinition = "TEXT")
    private String generatedUrl; // URL generada para acceder al audio

    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre; // Género principal

    @ManyToOne
    @JoinColumn(name = "subgenre_id")
    private Subgenre subgenre; // Subgénero específico (opcional)
}
