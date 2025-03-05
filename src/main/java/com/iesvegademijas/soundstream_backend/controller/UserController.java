package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

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
    @PostMapping
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // Verificar si el email ya existe
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está registrado.");
        }

        User newUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
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
}
