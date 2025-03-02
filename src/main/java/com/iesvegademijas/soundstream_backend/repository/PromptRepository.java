package com.iesvegademijas.soundstream_backend.repository;
import com.iesvegademijas.soundstream_backend.model.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "prompts", path = "prompts")
public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
