package com.iesvegademijas.soundstream_backend.repository;

import com.iesvegademijas.soundstream_backend.model.Song;
import com.iesvegademijas.soundstream_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "songs", path = "songs")
public interface SongRepository extends JpaRepository<Song, Long> {

    // 🔹 Obtener todas las canciones dentro de una playlist
    List<Song> findByUser_Id(Long userId);  // ✅ Esto usa el campo `user.id` internamente

    // 🔹 Filtrar canciones dentro de una playlist con múltiples criterios
    @Query("SELECT DISTINCT s FROM Song s " +
            "LEFT JOIN s.genre g " +
            "LEFT JOIN s.subgenre sub " +
            "LEFT JOIN s.instruments i " +
            "WHERE s.user.id = :playlistId " +
            "AND (:genreId IS NULL OR g.id = :genreId) " +
            "AND (:subgenreId IS NULL OR sub.id = :subgenreId) " +
            "AND (:minDuration IS NULL OR s.duration >= :minDuration) " +
            "AND (:maxDuration IS NULL OR s.duration <= :maxDuration) " +
            "AND (:instrumentIds IS NULL OR i.id IN :instrumentIds)")
    List<Song> findByFilters(
            @Param("playlistId") Long playlistId,
            @Param("genreId") Long genreId,
            Long subgenreId, @Param("minDuration") Double minDuration,
            @Param("maxDuration") Double maxDuration,
            @Param("instrumentIds") List<Long> instrumentIds
    );

    void deleteAllByUser(User user);
}

