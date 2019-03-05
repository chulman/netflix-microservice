package com.chulman.microservice.api.service;

import com.chulman.microservice.api.apns.ApnsConnector;
import com.chulman.microservice.api.apns.ApnsResponseHandler;
import com.chulman.microservice.api.utils.ApnsHeader;
import com.chulman.microservice.notification.domain.model.Notification;
import com.chulman.microservice.notification.domain.model.NotificationResult;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApnsProvider {

    @Autowired
    private ApnsConnector apnsConnector;

    @Autowired
    private ApnsResponseHandler apnsResponseHandler;

    public Observable<Void> send(Notification notification) {

        notification.setApns_id(UUID.randomUUID().toString());

        ChannelFuture future = apnsConnector.send(notification, getHeader(notification));
        return Observable.from(future).filter(aVoid -> future.isSuccess())
                                      .doOnNext(aVoid -> log.info("send future : {}", future.isSuccess()))
                                      .doOnError(throwable -> throwable.getCause())
                                      .subscribeOn(Schedulers.io());
    }

    private HttpHeaders getHeader(Notification notification) {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(ApnsHeader.AUTHORIZATION, "Bearer " + apnsConnector.getToken());
        headers.add(ApnsHeader.ID, notification.getApns_id());
        headers.add(ApnsHeader.TOPIC, notification.getBundle());
        headers.add(ApnsHeader.PRIORITY, 10);
//        headers.add(ApnsHeader.EXPIRATION, expire);
//        headers.add(ApnsHeader.COLLAPSE, collapse);

        return headers;
    }
}
