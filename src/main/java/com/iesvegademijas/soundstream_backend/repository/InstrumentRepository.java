package com.iesvegademijas.soundstream_backend.repository;
import com.iesvegademijas.soundstream_backend.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "Instruments", path = "Instruments")
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    @Query("SELECT i FROM Instrument i WHERE LOWER(i.name) IN :names")
    List<Instrument> findByNameIn(@Param("names") List<String> names);
}
