package com.example.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class WeatherServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WeatherServiceApplication.class, args);
        log.info("Weather Service started successfully!");
        log.info("Server URL: http://localhost:8080");
        log.info("Client endpoints:");
        log.info("  - GET http://localhost:8080/api/weather/client/forecast/{city}");
        log.info("  - GET http://localhost:8080/api/weather/client/test");
        log.info("Server endpoints:");
        log.info("  - GET http://localhost:8080/api/weather/server/city/{city}");
        log.info("  - GET http://localhost:8080/api/weather/server/test");
    }
}