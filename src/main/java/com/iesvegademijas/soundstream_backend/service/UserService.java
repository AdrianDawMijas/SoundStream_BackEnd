package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.*;
import com.iesvegademijas.soundstream_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User createUser(User user) {
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getSubscription() == null) {
            Subscription freeSubscription = subscriptionService.createSubscription(SubscriptionType.FREE);
            user.setSubscription(subscriptionRepository.save(freeSubscription));
        }

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
