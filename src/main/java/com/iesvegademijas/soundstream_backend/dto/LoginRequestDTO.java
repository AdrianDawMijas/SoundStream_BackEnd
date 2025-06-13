package com.iesvegademijas.soundstream_backend.dto;

import lombok.Data;

/**
 * DTO para solicitud de login.
 */
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
    private String nombre; // ⚠️ Puede estar sobrando si no se usa en el login
}
