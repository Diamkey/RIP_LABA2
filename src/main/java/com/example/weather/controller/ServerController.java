package com.example.weather.controller;

import com.example.weather.model.WeatherData;
import com.example.weather.service.WeatherServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/weather/server")
@Slf4j
public class ServerController {

    private final WeatherServerService weatherService;

    public ServerController(WeatherServerService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/city/{cityName}")
    public Mono<ResponseEntity<WeatherData>> getWeather(@PathVariable String cityName) {
        log.info("Received request for city: {}", cityName);
        return weatherService.getWeatherForCity(cityName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public Flux<WeatherData> getAllWeather() {
        log.info("Received request for all weather data");
        return weatherService.getAllWeatherData()
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("Weather Server is running!");
    }
}