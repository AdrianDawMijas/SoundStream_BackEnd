package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    private String name;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public Subgenre(String subgenre, Genre genre) {
        this.genre = genre;
        this.name = subgenre;
    }
}
