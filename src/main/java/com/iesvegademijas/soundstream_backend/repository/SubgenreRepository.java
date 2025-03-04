package com.iesvegademijas.soundstream_backend.repository;

import com.iesvegademijas.soundstream_backend.model.Subgenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "SubGenres", path = "SubGenres")
public interface SubgenreRepository extends JpaRepository<Subgenre, Long> {
    Optional<Subgenre> findByNameIgnoreCase(String name);
}
