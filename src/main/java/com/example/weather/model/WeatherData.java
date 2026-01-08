package com.example.weather.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private Long id;
    private String city;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private String description;
    private LocalDateTime timestamp;
    private Double windSpeed;
    private String windDirection;
    private Integer visibility;
    private Double feelsLike;
}