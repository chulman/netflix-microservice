package com.chulman.microservice.api.apns;

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

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApnsProvider {

    @Autowired
    private ApnsConnector apnsConnector;

    public ChannelFuture send(Notification notification) {
        return apnsConnector.send(notification, getHeader(notification.getBundle()));
    }

    private HttpHeaders getHeader(String bundleID) {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(ApnsHeader.AUTHORIZATION, "Bearer " + apnsConnector.getToken());
        headers.add(ApnsHeader.ID, UUID.randomUUID().toString());
        headers.add(ApnsHeader.TOPIC, bundleID);
        headers.add(ApnsHeader.PRIORITY, 10);
//        headers.add(ApnsHeader.EXPIRATION, expire);
//        headers.add(ApnsHeader.COLLAPSE, collapse);

        return headers;
    }
}
