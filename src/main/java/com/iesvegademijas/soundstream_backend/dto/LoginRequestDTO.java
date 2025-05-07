package com.iesvegademijas.soundstream_backend.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
    private String nombre;

}
