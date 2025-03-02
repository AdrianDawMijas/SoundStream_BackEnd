package com.iesvegademijas.soundstream_backend.controller;

import com.iesvegademijas.soundstream_backend.model.Prompt;
import com.iesvegademijas.soundstream_backend.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Prompts", description = "Gestión de prompts enviados por los usuarios")
@RestController
@RequestMapping("/v1/api/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    // Obtener todos los prompts
    @GetMapping
    public ResponseEntity<List<Prompt>> getAllPrompts() {
        return ResponseEntity.ok(promptService.getAllPrompts());
    }

    // Obtener un prompt por ID
    @GetMapping("/{id}")
    public ResponseEntity<Prompt> getPromptById(@PathVariable Long id) {
        Optional<Prompt> prompt = promptService.getPromptById(id);
        return prompt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener todos los prompts de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Prompt>> getPromptsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(promptService.getPromptsByUser(userId));
    }

    // Guardar un nuevo prompt
    @PostMapping("/{userId}")
    public ResponseEntity<Prompt> savePrompt(@PathVariable Long userId, @RequestBody Prompt prompt) {
        Prompt savedPrompt = promptService.savePrompt(userId, prompt.getText(), prompt.getGeneratedDescription());
        return ResponseEntity.ok(savedPrompt);
    }

    // Eliminar un prompt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrompt(@PathVariable Long id) {
        promptService.deletePrompt(id);
        return ResponseEntity.noContent().build();
    }
}
