package com.example.weather.service;

import com.example.weather.dto.WeatherResponse;
import com.example.weather.model.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class WeatherClientService {

    private final WebClient webClient;

    public WeatherClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<WeatherResponse> getWeather(String city) {
        log.info("Requesting weather for city: {}", city);

        return webClient.get()
                .uri("/api/weather/server/city/{cityName}", city)
                .retrieve()
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Server error"))
                )
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new RuntimeException("City not found"))
                )
                .bodyToMono(WeatherData.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .doBeforeRetry(retrySignal ->
                                log.warn("Retrying request for city: {}, attempt: {}",
                                        city, retrySignal.totalRetries() + 1))
                )
                .timeout(Duration.ofSeconds(10))
                .map(weatherData -> new WeatherResponse(
                        weatherData.getCity(),
                        weatherData.getTemperature(),
                        weatherData.getDescription(),
                        "SUCCESS"
                ))
                .onErrorResume(e -> {
                    log.error("Error fetching weather for {}: {}", city, e.getMessage());
                    return Mono.just(new WeatherResponse(
                            city,
                            null,
                            "Service unavailable",
                            "ERROR"
                    ));
                })
                .doOnSuccess(response ->
                        log.info("Successfully fetched weather for {}: {}°C",
                                city, response.getTemperature()))
                .doOnError(error ->
                        log.error("Failed to fetch weather for {}: {}", city, error.getMessage()));
    }

    public Mono<String> testConnection() {
        return webClient.get()
                .uri("/api/weather/server/test")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> Mono.just("Connection failed: " + e.getMessage()));
    }
}