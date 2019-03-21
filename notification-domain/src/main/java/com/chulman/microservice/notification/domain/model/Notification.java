package com.chulman.microservice.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    String createdAt = "";
    long id;
    String apns_id;
    String name;
    String os;
    String bundle;
    String deviceToken;


    Map<String,Object> payload = new HashMap<>();

}

