package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType type; // FREE, PERSONAL, PRO

    @Column(nullable = false)
    private LocalDateTime startDate; // Fecha de inicio de la suscripción

    @Column(nullable = true)
    private LocalDateTime endDate; // Fecha de expiración (null si es FREE)

    @Column(nullable = false)
    private Integer maxTracksPerDay; // Creaciones diarias

    @Column(nullable = false)
    private Integer maxTracksPerMonth; // Creaciones mensuales

    @Column(nullable = false)
    private Integer maxDownloadsPerDay; // Descargas diarias

    @Column(nullable = false)
    private Integer maxDownloadsPerMonth; // Descargas mensuales

    @Column(nullable = false)
    private Double maxTrackLength; // Duración máxima de la canción en minutos

    @Column(nullable = false)
    private Boolean priorityQueue; // Acceso prioritario a generación de IA

    @Column(nullable = false)
    private Boolean highQualityAudio; // MP3/WAV en alta calidad

    @Column(nullable = false)
    private Boolean commercialUse; // Uso comercial permitido

    @OneToOne(mappedBy = "subscription")
    private User user;
}
