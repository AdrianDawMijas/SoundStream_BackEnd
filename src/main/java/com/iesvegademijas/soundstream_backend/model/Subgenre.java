package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subgenres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subgenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nombre del subgénero

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre; // Género al que pertenece

    public Subgenre(String name, Genre genre) {
        this.name = name;
        this.genre = genre;
    }
}
