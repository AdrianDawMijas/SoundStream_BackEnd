package com.iesvegademijas.soundstream_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private SubscriptionType type;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Integer maxTracksPerDay;
    private Integer maxTracksPerMonth;
    private Integer maxDownloadsPerDay;
    private Integer maxDownloadsPerMonth;

    private Double maxTrackLength;       // Duración máxima de cada canción
    private Boolean highQualityAudio;    // ¿Permite audio en alta calidad?
    private Boolean commercialUse;       // ¿Permite uso comercial?

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private User user;
}
