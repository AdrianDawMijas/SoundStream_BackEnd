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

    @PostMapping("/{userId}")
    public ResponseEntity<User> assignSubscriptionToUser(
            @PathVariable Long userId, @RequestParam SubscriptionType type) {

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();

        if (user.getSubscription() != null) {
            subscriptionService.deleteSubscription(user.getSubscription().getId());
        }

        Subscription newSubscription = subscriptionService.createSubscription(type);
        user.setSubscription(newSubscription);
        newSubscription.setUser(user);

        userService.createUser(user);
        subscriptionService.saveSubscription(newSubscription);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(id);

        if (subscriptionOpt.isEmpty()) return ResponseEntity.notFound().build();

        Subscription subscription = subscriptionOpt.get();
        User user = subscription.getUser();

        if (user != null) {
            Subscription free = subscriptionService.createSubscription(SubscriptionType.FREE);
            user.setSubscription(free);
            subscriptionService.saveSubscription(free);
            userService.createUser(user);
        }

        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
