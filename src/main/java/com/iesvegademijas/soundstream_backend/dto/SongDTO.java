package com.iesvegademijas.soundstream_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Double duration;
    private Integer tempo;
    private String instruments;
    private String promptText; // Puede venir vacío
    private String genre;  // Recibido como String desde Angular
    private String subgenre; // Recibido como String desde Angular (puede ser null)
}