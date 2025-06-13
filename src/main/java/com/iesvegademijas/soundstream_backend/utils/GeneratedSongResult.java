package com.iesvegademijas.soundstream_backend.utils;

import lombok.Getter;

/**
 * DTO simple que encapsula el resultado de una canción generada por IA.
 * Contiene el título y la URL del archivo de audio generado.
 */
@Getter
public class GeneratedSongResult {

    private final String title;
    private final String url;

    /**
     * Constructor completo.
     *
     * @param title Título de la canción generada.
     * @param url   URL donde se encuentra el archivo de audio.
     */
    public GeneratedSongResult(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
