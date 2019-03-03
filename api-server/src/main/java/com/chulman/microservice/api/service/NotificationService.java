package com.chulman.microservice.api.service;

import com.chulman.microservice.api.apns.ApnsProvider;
import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.repository.NotificationRepository;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

    public Observable<Integer> sendToApns(Notification notification){
        Observable insertObservable = notificationRepository.insert(notification);
        ChannelFuture future = apnsProvider.send(notification);

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("send : {}", future.isSuccess());

                if(future.isSuccess()){
                    insertObservable.subscribe();

                    apnsProvider.
                }else{
                    log.error("send error: {}", future.cause().getMessage());
                }
            }
        });
        return insertObservable;
    }
}
