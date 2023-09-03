package com.example.sulsul;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "https://takgyun.shop", description = "SULSUL Test Server")})
@SpringBootApplication
public class SulsulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SulsulApplication.class, args);
	}

}