package com.example.bookingservice.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(HttpMessageConverters messageConverters) {
        // Принудительно добавляем все конвертеры (включая JSON) в билдер
        return RestClient.builder()
                .messageConverters(messageConverters.getConverters()::addAll);
    }
}