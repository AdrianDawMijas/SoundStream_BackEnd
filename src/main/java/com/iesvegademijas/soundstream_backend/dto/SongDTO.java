package com.iesvegademijas.soundstream_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Double duration;
    private Integer tempo;
    private List<String> instrumentNames = new ArrayList<>(); // ✅ Inicialización mutable
    private String promptText;
    private String genre;
    private String subgenre;
    private Long userId;
    private String title;
}
