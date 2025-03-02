package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.Subscription;
import com.iesvegademijas.soundstream_backend.model.SubscriptionType;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.service.SubscriptionService;
import com.iesvegademijas.soundstream_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Suscripciones", description = "Gestión de suscripciones de usuarios")
@RestController
@RequestMapping("/v1/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    // Crear una nueva suscripción sin asociarla a un usuario
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestParam SubscriptionType type) {
        Subscription subscription = subscriptionService.createSubscription(type);
        return ResponseEntity.ok(subscription);
    }

    // Asociar una suscripción a un usuario
    @PostMapping("/{userId}")
    public ResponseEntity<Subscription> assignSubscriptionToUser(
            @PathVariable Long userId, @RequestParam SubscriptionType type) {

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Subscription subscription = subscriptionService.createSubscription(type);
        user.setSubscription(subscription);
        subscription.setUser(user);

        return ResponseEntity.ok(subscriptionService.saveSubscription(subscription));
    }

    // Obtener una suscripción por ID
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una suscripción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
