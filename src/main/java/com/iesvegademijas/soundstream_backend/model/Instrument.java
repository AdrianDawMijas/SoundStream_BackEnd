package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instruments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Nombre del instrumento (Piano, Guitarra, etc.)

    private String category; // Categoría del instrumento (percusión, cuerda, viento...)

    @Column(columnDefinition = "TEXT")
    private String description;

    public Instrument(String name) {
        this.name = name;
    }
}
