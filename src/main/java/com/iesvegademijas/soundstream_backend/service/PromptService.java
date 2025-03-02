package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Prompt;
import com.iesvegademijas.soundstream_backend.model.User;
import com.iesvegademijas.soundstream_backend.repository.PromptRepository;
import com.iesvegademijas.soundstream_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromptService {

    @Autowired
    private PromptRepository promptRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Guarda un nuevo prompt generado por el usuario
     */
    public Prompt savePrompt(Long userId, String text, String generatedDescription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Prompt prompt = new Prompt();
        prompt.setText(text);
        prompt.setGeneratedDescription(generatedDescription);
        prompt.setUser(user);

        return promptRepository.save(prompt);
    }

    /**
     * Obtiene todos los prompts almacenados
     */
    public List<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }

    /**
     * Obtiene los prompts generados por un usuario en específico
     */
    public List<Prompt> getPromptsByUser(Long userId) {
        return promptRepository.findAll().stream()
                .filter(prompt -> prompt.getUser().getId().equals(userId))
                .toList();
    }

    /**
     * Obtiene un prompt por su ID
     */
    public Optional<Prompt> getPromptById(Long id) {
        return promptRepository.findById(id);
    }

    /**
     * Elimina un prompt por ID
     */
    public void deletePrompt(Long id) {
        promptRepository.deleteById(id);
    }
}
