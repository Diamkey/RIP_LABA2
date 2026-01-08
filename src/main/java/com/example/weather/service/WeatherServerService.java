package com.example.weather.service;

import com.example.weather.model.WeatherData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class WeatherServerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<WeatherData> getWeatherForCity(String city) {
        // НЕОПТИМАЛЬНАЯ ЛОГИКА:
        // 1. Каждый раз создаём огромный список (10000+ городов)
        List<WeatherData> weatherDataList = generateLargeWeatherDataList();
        log.info("Generated {} weather records", weatherDataList.size());

        // 2. Линейный поиск в списке O(n)
        WeatherData found = null;
        for (WeatherData data : weatherDataList) {
            if (data.getCity().equalsIgnoreCase(city)) {
                found = data;
                break;
            }
        }

        // 3. Несколько ненужных преобразований и сортировок
        if (found != null) {
            // Преобразуем список в Set и обратно (лишняя операция)
            Set<WeatherData> set = new HashSet<>(weatherDataList);
            List<WeatherData> backToList = new ArrayList<>(set);

            // Двойная сортировка (лишние операции)
            backToList.sort(Comparator.comparing(WeatherData::getTemperature));
            backToList.sort(Comparator.comparing(WeatherData::getCity));

            // 4. Многократная сериализация/десериализация JSON
            performMultipleSerializations(found);

            return Mono.just(found);
        }

        // Если город не найден, создаём новый (также неоптимально)
        return Mono.just(generateRandomWeatherData(city));
    }

    private List<WeatherData> generateLargeWeatherDataList() {
        List<String> cities = Arrays.asList(
                "Moscow", "London", "Paris", "Berlin", "Madrid", "Rome",
                "Tokyo", "Beijing", "New York", "Los Angeles", "Sydney", "Cairo"
        );

        // Генерируем 10000+ записей
        List<WeatherData> dataList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String city = cities.get(i % cities.size()) + (i / cities.size() > 0 ? "-" + (i / cities.size()) : "");
            dataList.add(generateRandomWeatherData(city));
        }
        return dataList;
    }

    private WeatherData generateRandomWeatherData(String city) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new WeatherData(
                (long) random.nextInt(1, 1000000),
                city,
                random.nextDouble(-20, 40),
                random.nextDouble(30, 100),
                random.nextDouble(980, 1040),
                getRandomDescription(random),
                LocalDateTime.now().minusHours(random.nextInt(0, 72)),
                random.nextDouble(0, 25),
                getRandomDirection(random),
                random.nextInt(1000, 10000),
                random.nextDouble(-25, 45)
        );
    }

    private String getRandomDescription(ThreadLocalRandom random) {
        String[] descriptions = {"Sunny", "Cloudy", "Rainy", "Snowy", "Foggy", "Windy"};
        return descriptions[random.nextInt(descriptions.length)];
    }

    private String getRandomDirection(ThreadLocalRandom random) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[random.nextInt(directions.length)];
    }

    private void performMultipleSerializations(WeatherData data) {
        try {
            // НЕОПТИМАЛЬНО: сериализуем и десериализуем 5 раз
            for (int i = 0; i < 5; i++) {
                String json = objectMapper.writeValueAsString(data);
                WeatherData deserialized = objectMapper.readValue(json, WeatherData.class);
                log.debug("Serialization/deserialization round {}", i + 1);
            }
        } catch (JsonProcessingException e) {
            log.error("Error during serialization", e);
        }
    }

    // Метод для получения всех данных (необязательно)
    public Mono<List<WeatherData>> getAllWeatherData() {
        List<WeatherData> data = generateLargeWeatherDataList();
        return Mono.just(data);
    }
}