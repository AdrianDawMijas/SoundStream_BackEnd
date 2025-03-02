package com.iesvegademijas.soundstream_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @EqualsAndHashCode.Exclude
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String password;

        @OneToOne
        @JoinColumn(name = "subscription_id", nullable = false)
        private Subscription subscription; // Aquí está la info de su tipo de cuenta

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private Playlist library; // La biblioteca del usuario (su única playlist)


}


