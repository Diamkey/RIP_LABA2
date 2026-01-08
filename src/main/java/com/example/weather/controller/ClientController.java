package com.example.weather.controller;

import com.example.weather.dto.WeatherResponse;
import com.example.weather.service.WeatherClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/weather/client")
@Slf4j
public class ClientController {

    private final WeatherClientService weatherClientService;

    public ClientController(WeatherClientService weatherClientService) {
        this.weatherClientService = weatherClientService;
    }

    @GetMapping("/forecast/{city}")
    public Mono<ResponseEntity<WeatherResponse>> getWeatherForecast(@PathVariable String city) {
        log.info("Client request for weather forecast in: {}", city);

        return weatherClientService.getWeather(city)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public Mono<ResponseEntity<String>> testConnection() {
        return weatherClientService.testConnection()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/batch/{cities}")
    public Mono<ResponseEntity<String>> getBatchWeather(@PathVariable String cities) {
        String[] cityArray = cities.split(",");

        StringBuilder result = new StringBuilder();
        return Flux.fromArray(cityArray)
                .flatMap(city -> weatherClientService.getWeather(city.trim())
                        .map(response -> String.format("%s: %.1f°C, %s%n",
                                response.getCity(),
                                response.getTemperature(),
                                response.getDescription()))
                )
                .collectList()
                .map(list -> {
                    list.forEach(result::append);
                    return ResponseEntity.ok(result.toString());
                });
    }
}