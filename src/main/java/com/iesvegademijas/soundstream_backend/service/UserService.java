package com.iesvegademijas.soundstream_backend.service;


import com.iesvegademijas.soundstream_backend.model.Subscription;
import com.iesvegademijas.soundstream_backend.model.SubscriptionType;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.repository.SubscriptionRepository;
import com.iesvegademijas.soundstream_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        if (user.getSubscription() == null) {
            Subscription freeSubscription = subscriptionService.createSubscription(SubscriptionType.FREE);
            freeSubscription = subscriptionRepository.save(freeSubscription); // 🔹 Guardar explícitamente
            user.setSubscription(freeSubscription);
        }
        return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
