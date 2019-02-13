package com.chulman.microservice.eureka.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/eureka/healthcheck")
    public String home(){
        return "Hello World";
    }
}
