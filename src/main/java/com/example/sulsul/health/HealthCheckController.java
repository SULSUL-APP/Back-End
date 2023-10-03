package com.example.sulsul.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/")
    public String healthCheck() {
        return "<h1>SULSUL Server Health Check</h1>";
    }
}