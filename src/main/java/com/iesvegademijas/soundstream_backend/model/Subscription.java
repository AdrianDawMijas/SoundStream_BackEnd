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
    private SubscriptionType type; // FREE, PERSONAL, PRO

    private LocalDateTime startDate; // Fecha de inicio de la suscripción
    private LocalDateTime endDate; // Fecha de expiración (null si es FREE)

    private Integer maxTracksPerDay; // Creaciones diarias
    private Integer maxTracksPerMonth; // Creaciones mensuales

    private Integer maxDownloadsPerDay; // Descargas diarias

    private Integer maxDownloadsPerMonth; // Descargas mensuales

    private Double maxTrackLength; // Duración máxima de la canción en minutos

    private Boolean priorityQueue; // Acceso prioritario a generación de IA
    private Boolean highQualityAudio; // MP3/WAV en alta calidad

    private Boolean commercialUse; // Uso comercial permitido

    @OneToOne(mappedBy = "subscription")
    private User user;
}
