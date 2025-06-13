package com.iesvegademijas.soundstream_backend.dto;

import com.iesvegademijas.soundstream_backend.model.User;
import lombok.Data;

/**
 * DTO para devolver datos del usuario.
 */
@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String nombre;
    private String subscriptionType;
    private String role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nombre = user.getName();
        this.subscriptionType = user.getSubscription() != null
                ? user.getSubscription().getType().name()
                : "N/A";
        this.role = user.getRole().name();
    }
}
