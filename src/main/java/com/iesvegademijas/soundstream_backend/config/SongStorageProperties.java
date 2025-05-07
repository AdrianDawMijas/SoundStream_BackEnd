package com.iesvegademijas.soundstream_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "musicgen.song")
public class SongStorageProperties {

    private String storagePath;
    private String baseUrl;

    // getters y setters
}
