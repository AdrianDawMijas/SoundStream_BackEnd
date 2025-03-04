package com.iesvegademijas.soundstream_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double duration;
    private Integer tempo;
    private String instruments;
    private String promptText;

    @Column(columnDefinition = "TEXT")  // 🔹 Permite URLs largas
    private String generatedUrl;


    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = true)
    private Playlist playlist;

    // 🔹 Relación con Genre (Género principal)
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    // 🔹 Relación con Subgenre (Subgénero específico)
    @ManyToOne
    @JoinColumn(name = "subgenre_id", nullable = true)
    private Subgenre subgenre;
}
