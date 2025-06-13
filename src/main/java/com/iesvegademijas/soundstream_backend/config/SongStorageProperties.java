package com.iesvegademijas.soundstream_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades para el almacenamiento de canciones generadas.
 * Se configura en application.properties con el prefijo 'song.storage'.
 */
@Configuration
@ConfigurationProperties(prefix = "song.storage")
@Data
public class SongStorageProperties {
    private String storagePath;
    private String baseUrl;
}
