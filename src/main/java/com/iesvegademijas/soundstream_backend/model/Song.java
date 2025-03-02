package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    private String name; // Nombre de la canción

    @Column(nullable = false, unique = true)
    private String url; // URL donde está almacenada la canción

    @Column(nullable = false)
    private Double duration; // Duración en minutos

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre; // Género de la canción

    @Column(nullable = false)
    private String subgenre; // Subgénero de la canción (ej: Rock -> Hard Rock)

    @Column(nullable = false)
    private Integer tempo; // BPM (beats por minuto)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Fecha de creación

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario que generó la canción

    @OneToOne
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt; // Prompt usado para generar la canción

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist; // Biblioteca donde está guardada esta canción

}
