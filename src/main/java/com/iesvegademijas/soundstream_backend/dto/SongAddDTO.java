package com.iesvegademijas.soundstream_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para añadir una canción a una lista u otra entidad.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongAddDTO {
    private Long songId;
}
