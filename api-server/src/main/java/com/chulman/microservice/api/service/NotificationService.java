package com.chulman.microservice.api.service;

import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Slf4j
@Service
public class NotificationService {

    @Autowired
    ApnsProvider apnsProvider;
    @Autowired
    NotificationRepository notificationRepository;


    public Observable<Integer> sendToApns(Notification notification) {
        return apnsProvider.send(notification)
                .switchMap(aVoid -> notificationRepository.insert(notification))
                .filter(integer -> integer==1);
    }
}
