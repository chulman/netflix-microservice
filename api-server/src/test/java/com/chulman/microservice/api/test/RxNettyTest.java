package com.chulman.microservice.api.test;

import com.chulman.microservice.notification.domain.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.reactivex.netty.protocol.http.client.HttpClient;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class RxNettyTest {

    @Test
    public void test() throws JsonProcessingException {

        Map<String, Object> payloads = new HashMap<>();
        Map<String, Object> aps = new HashMap<>();
        Map<String, String> alert = new HashMap<>();
        alert.put("title", "제목");
        alert.put("body", "내용");
        aps.put("alert", alert);
        aps.put("sounds", "default");
        payloads.put("aps", aps);


        Notification notification = Notification.builder().payload(payloads)
                                                            .name("chul")
                                                            .os("IOS")
                                                            .deviceToken("f606c721405d977eac0199a7b34d6701b988337441aa4fb3382f834bf9ac86fe")
                                                            .bundle("com.chulm.notification")
                                                            .build();


        Observable<ByteBuf> body = Observable.just(notification)
                .doOnError(e->System.err.println(e.getCause()))
                .doOnNext(notification1 -> System.err.println("some data read"))
                .observeOn(Schedulers.io())
                .map(b-> {
                    ByteBuf buf = null;
                    try {
                        buf =  Unpooled.copiedBuffer(new ObjectMapper().writeValueAsString(b).getBytes());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }finally {
                        return buf;
                    }
                });


        HttpClient.newClient("localhost",8081)
//                  .secure()
                  .createPost("/api/notification/v1/apns/send")
                  .addHeader("Content-Type", "application/json")
                  .writeContentAndFlushOnEach(body)
                  .observeOn(Schedulers.io())
                  .toBlocking()
                  .subscribe();
    }

}
