package com.chulman.microservice.api.configuration;

import com.chulman.microservice.api.apns.ApnsConnector;
import com.chulman.microservice.api.apns.ApnsResponseHandler;
import com.chulman.microservice.api.apns.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
@PropertySource(value = "classpath:/apns.properties")
public class ApnsConfig {

    @Value("${notification.apns.keyID}")
    String keyID;
    @Value("${notification.apns.teamID}")
    String teamID;
    @Value("${notification.apns.secret}")
    String secret;

    @Value("${notification.apns.production}")
    boolean production;

    @Value("${notification.apns.eventloopThreadCount}")
    int eventloopThreadCount;

    @Bean
    public JwtProvider getJwtProvider() {
        return new JwtProvider();
    }

    @Bean
    public ApnsResponseHandler getApnsResponseHandler() {
        return new ApnsResponseHandler();
    }

    @Bean
    public ApnsConnector getApnsConnector() throws Exception {

        String host = (production == true) ? "api.push.apple.com" : "api.development.push.apple.com";

        ApnsConnector apnsConnector = new ApnsConnector(getApnsResponseHandler());
        apnsConnector.setToken(getJwtProvider().createToken(keyID,teamID,secret));
        apnsConnector.setEventloopThreadCount(eventloopThreadCount);
        apnsConnector.setHost(host);
        apnsConnector.connect();
        return apnsConnector;
    }
}
