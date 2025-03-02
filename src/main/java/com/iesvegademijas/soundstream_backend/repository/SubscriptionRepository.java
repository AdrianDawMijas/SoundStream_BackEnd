package com.iesvegademijas.soundstream_backend.repository;
import com.iesvegademijas.soundstream_backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "subscriptions", path = "subscriptions")
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
