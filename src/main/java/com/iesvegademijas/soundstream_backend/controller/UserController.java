package com.iesvegademijas.soundstream_backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.iesvegademijas.soundstream_backend.dto.LoginRequestDTO;
import com.iesvegademijas.soundstream_backend.dto.LoginResponseDTO;
import com.iesvegademijas.soundstream_backend.dto.RegisterRequestDTO;
import com.iesvegademijas.soundstream_backend.dto.UserResponseDTO;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.repository.SubscriptionRepository;
import com.iesvegademijas.soundstream_backend.service.JwtService;
import com.iesvegademijas.soundstream_backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Tag(name = "Usuarios", description = "Operaciones de usuario")
@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // ─────────────────────────────────────────────────────────────
    // 📌 REGISTRO
    // ─────────────────────────────────────────────────────────────

    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequestDTO dto) {
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado.");
        }

        User user = new User();
        user.setName(dto.getNombre());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Codifica la contraseña

        User newUser = userService.createUser(user);
        String token = jwtService.generateToken(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(newUser, token));
    }

    // ─────────────────────────────────────────────────────────────
    // 🔐 LOGIN TRADICIONAL
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<User> optionalUser = userService.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(user, token));
    }

    // ─────────────────────────────────────────────────────────────
    // 🔐 LOGIN CON GOOGLE
    // ─────────────────────────────────────────────────────────────

    @PostMapping("/login/google-token")
    public ResponseEntity<?> loginWithGoogleToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token no proporcionado");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(List.of("1056065714806-qnno38lp4gh2lvnmthnufkcpibd7m5tj.apps.googleusercontent.com")) // ← sustituye por tu clientId real
                    .build();

            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                String email = idToken.getPayload().getEmail();

                User user = userService.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword(UUID.randomUUID().toString());
                    return userService.createUser(newUser);
                });

                String jwt = jwtService.generateToken(user);
                return ResponseEntity.ok(new LoginResponseDTO(user, jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verificando token: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 📦 CRUD USUARIOS (ADMIN o PERFIL)
    // ─────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> dtos = userService.getAllUsers().stream()
                .map(UserResponseDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.getUserById(id).map(user -> {
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
            if (userDetails.getPassword() != null) user.setPassword(passwordEncoder.encode(userDetails.getPassword()));

            // ✅ Añade esto:
            if (userDetails.getRole() != null) user.setRole(userDetails.getRole());
            if (userDetails.getSubscription() != null && userDetails.getSubscription().getId() != null) {
                subscriptionRepository.findById(userDetails.getSubscription().getId())
                        .ifPresent(user::setSubscription);
            }

            return ResponseEntity.ok(userService.createUser(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
