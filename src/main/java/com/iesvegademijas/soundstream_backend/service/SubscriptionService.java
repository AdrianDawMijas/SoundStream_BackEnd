package com.iesvegademijas.soundstream_backend.service;

import com.iesvegademijas.soundstream_backend.model.Subscription;
import com.iesvegademijas.soundstream_backend.model.SubscriptionType;
import com.iesvegademijas.soundstream_backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(SubscriptionType type) {
        Subscription subscription = new Subscription();
        subscription.setType(type);
        subscription.setStartDate(LocalDateTime.now());

        switch (type) {
            case FREE -> {
                subscription.setMaxTracksPerDay(15);
                subscription.setMaxTracksPerMonth(null);
                subscription.setMaxDownloadsPerDay(1);
                subscription.setMaxDownloadsPerMonth(null);
                subscription.setMaxTrackLength(0.5);
                subscription.setPriorityQueue(false);
                subscription.setHighQualityAudio(false);
                subscription.setCommercialUse(false);
                subscription.setEndDate(null);
            }
            case PERSONAL -> {
                subscription.setMaxTracksPerDay(null);
                subscription.setMaxTracksPerMonth(900);
                subscription.setMaxDownloadsPerDay(null);
                subscription.setMaxDownloadsPerMonth(300);
                subscription.setMaxTrackLength(2.5);
                subscription.setPriorityQueue(true);
                subscription.setHighQualityAudio(true);
                subscription.setCommercialUse(false);
                subscription.setEndDate(LocalDateTime.now().plusMonths(12));
            }
            case PRO -> {
                subscription.setMaxTracksPerDay(null);
                subscription.setMaxTracksPerMonth(3000);
                subscription.setMaxDownloadsPerDay(null);
                subscription.setMaxDownloadsPerMonth(500);
                subscription.setMaxTrackLength(7.0);
                subscription.setPriorityQueue(true);
                subscription.setHighQualityAudio(true);
                subscription.setCommercialUse(true);
                subscription.setEndDate(LocalDateTime.now().plusMonths(12));
            }
        }

        return subscriptionRepository.save(subscription);
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }
}
