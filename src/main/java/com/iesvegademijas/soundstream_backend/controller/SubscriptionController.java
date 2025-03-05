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

    /**
     * 🔹 Asigna una suscripción a un usuario.
     * ⚠️ Si el usuario ya tiene una suscripción, la reemplaza.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<User> assignSubscriptionToUser(
            @PathVariable Long userId, @RequestParam SubscriptionType type) {

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        // ✅ Verificar si ya tiene una suscripción y eliminarla antes de asignar la nueva
        if (user.getSubscription() != null) {
            subscriptionService.deleteSubscription(user.getSubscription().getId());
        }

        Subscription newSubscription = subscriptionService.createSubscription(type);
        user.setSubscription(newSubscription);
        newSubscription.setUser(user);

        // ✅ Guardar los cambios en usuario y suscripción
        userService.createUser(user);
        subscriptionService.saveSubscription(newSubscription);

        return ResponseEntity.ok(user);
    }

    /**
     * 🔹 Obtiene una suscripción por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 🔹 Elimina una suscripción y reasigna al usuario una suscripción FREE.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(id);

        if (subscriptionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Subscription subscription = subscriptionOpt.get();
        User user = subscription.getUser();

        // ✅ Si el usuario tenía la suscripción, se le asigna una FREE antes de eliminarla
        if (user != null) {
            Subscription freeSubscription = subscriptionService.createSubscription(SubscriptionType.FREE);
            user.setSubscription(freeSubscription);
            subscriptionService.saveSubscription(freeSubscription);
            userService.createUser(user);
        }

        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
