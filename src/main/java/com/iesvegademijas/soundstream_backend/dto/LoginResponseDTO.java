package com.iesvegademijas.soundstream_backend.dto;

import com.iesvegademijas.soundstream_backend.model.User;
import lombok.Data;

/**
 * DTO de respuesta tras un login exitoso.
 */
@Data
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String subscriptionType;
    private String nombre;
    private String role;
    private String token;

    public LoginResponseDTO(User user, String token) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.subscriptionType = user.getSubscription().getType().name();
        this.nombre = user.getName();
        this.role = user.getRole().name();
        this.token = token;
    }
}
