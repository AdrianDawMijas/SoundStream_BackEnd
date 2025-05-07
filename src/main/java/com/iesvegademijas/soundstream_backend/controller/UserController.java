package com.iesvegademijas.soundstream_backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.iesvegademijas.soundstream_backend.dto.LoginRequestDTO;
import com.iesvegademijas.soundstream_backend.dto.LoginResponseDTO;
import com.iesvegademijas.soundstream_backend.dto.RegisterRequestDTO;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Usuarios", description = "Operaciones de usuario")
@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ✅ Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Crear un nuevo usuario con una suscripción por defecto
    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequestDTO dto) {
        Optional<User> existingUser = userService.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setName(dto.getNombre());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(newUser));
    }

    // ✅ Actualizar datos de un usuario (sin sobrescribir con null)
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.getUserById(id).map(user -> {
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
            if (userDetails.getPassword() != null) user.setPassword(userDetails.getPassword());

            userService.createUser(user);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Eliminar un usuario
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<User> optionalUser = userService.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        User user = optionalUser.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        return ResponseEntity.ok(new LoginResponseDTO(user));
    }

    @PostMapping("/login/google-token")
    public ResponseEntity<?> loginWithGoogleToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(List.of("1056065714806-qnno38lp4gh2lvnmthnufkcpibd7m5tj.apps.googleusercontent.com")) // 🔁 sustituye por tu client_id real
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                GoogleIdToken.Payload googlePayload = idToken.getPayload();
                String email = googlePayload.getEmail();

                User user = userService.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword(UUID.randomUUID().toString()); // Placeholder
                    return userService.createUser(newUser);
                });

                return ResponseEntity.ok(new LoginResponseDTO(user));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verificando token: " + e.getMessage());
        }
    }

}
