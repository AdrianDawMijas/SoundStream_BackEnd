package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "prompts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text; // Prompt que ingresó el usuario

    @Column(nullable = true, length = 2000)
    private String generatedDescription; // Descripción generada automáticamente

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Usuario que hizo la petición
}
