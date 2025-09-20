package com.starbucks.menuaichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StarbucksMenuAiChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarbucksMenuAiChatApplication.class, args);
    }
}