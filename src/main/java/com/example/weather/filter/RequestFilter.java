package com.example.weather.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2)
@Slf4j
public class RequestFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Добавляем кастомные заголовки к ответу
        exchange.getResponse().getHeaders().add("X-Weather-Service", "v1.0");
        exchange.getResponse().getHeaders().add("X-Response-Time", String.valueOf(System.currentTimeMillis()));

        // Логируем запрос
        log.debug("Processing request to: {}", exchange.getRequest().getURI().getPath());

        return chain.filter(exchange);
    }
}