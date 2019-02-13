package com.chulman.microservice.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/api/healthcheck")
    public String HealthCheck(){
        return "Hello World";
    }

    @GetMapping("/api/member/{id}")
    public String getMember(@PathVariable long id) throws JsonProcessingException {

        Member member = new Member();
        member.setId(id);
        member.setName("chulman");
        return MAPPER.writeValueAsString(member);
    }
}

class Member{
    long id;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}