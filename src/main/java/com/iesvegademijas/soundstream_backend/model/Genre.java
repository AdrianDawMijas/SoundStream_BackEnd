package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name; // Nombre del género
}
