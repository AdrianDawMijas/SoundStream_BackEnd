package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    private String name; // Nombre de la playlist

    @Column(nullable = true)
    private String description; // Descripción opcional

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Dueño de la biblioteca

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs; // Canciones dentro de la biblioteca

    @Column(nullable = false)
    private LocalDateTime createdAt; // Fecha de creación
}
