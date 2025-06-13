package com.iesvegademijas.soundstream_backend.repository;

import com.iesvegademijas.soundstream_backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "Genres", path = "Genres")
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);;
}
