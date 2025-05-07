package com.iesvegademijas.soundstream_backend.dto;

import com.iesvegademijas.soundstream_backend.model.User;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String subscriptionType;
    private String nombre; // ✅ nuevo

    public LoginResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.subscriptionType = user.getSubscription().getType().name();
        this.nombre = user.getName(); // ✅ suponer que el User tiene campo nombre
    }
}
