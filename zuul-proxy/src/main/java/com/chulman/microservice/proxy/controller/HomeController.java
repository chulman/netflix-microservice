package com.chulman.microservice.proxy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/proxy/healthcheck")
    public String home(){
        return "hello world";
    }
}
