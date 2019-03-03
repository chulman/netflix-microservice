package com.chulman.microservice.notification;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.chulman.microservice.notification.*")
public @interface EnableNotificationDomain {
}