package com.chulman.microservice.api.test;

import com.chulman.microservice.notification.domain.model.Notification;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

public class NotificationTest {

    @Test
    public void send(){
        long startTime = System.currentTimeMillis();

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8081/api/notification/v1/apns/send";
        URI uri = URI.create(url);

        //setting max stream.
        int size = 500;

        for (int i = 0; i < size; i++) {

            Map<String,Object> payloads = new HashMap<>();
            Map<String,Object> aps = new HashMap<>();
            Map<String,String> alert = new HashMap<>();
            alert.put("title","제목"+i);
            alert.put("body","내용"+i);
            aps.put("alert",alert);
            aps.put("sounds","default");
            payloads.put("aps", aps);


            Notification notification = Notification.builder().payload(payloads)
                                                                .name("chul")
                                                                .os("IOS")
                                                                .deviceToken("f606c721405d977eac0199a7b34d6701b988337441aa4fb3382f834bf9ac86fe")
                                                                .bundle("com.chulm.notification")
                                                                .build();

            Integer rs = restTemplate.postForObject(uri, notification, Integer.class);
        }
        System.err.println("end time ="+(System.currentTimeMillis()-startTime));
    }
}
