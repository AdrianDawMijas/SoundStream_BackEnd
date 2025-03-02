package com.iesvegademijas.soundstream_backend.repository;
import com.iesvegademijas.soundstream_backend.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "playlists", path = "playlists")
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
