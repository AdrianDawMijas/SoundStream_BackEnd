package com.iesvegademijas.soundstream_backend.repository;

import com.iesvegademijas.soundstream_backend.model.Genre;
import com.iesvegademijas.soundstream_backend.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "songs", path = "songs")
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByUserId(Long userId);
    List<Song> findByGenre(Genre genre);
}
