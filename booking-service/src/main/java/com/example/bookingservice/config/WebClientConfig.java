package com.example.bookingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(ObjectMapper objectMapper) {
        // Создаем конвертер на основе ObjectMapper (где настроена поддержка LocalDateTime)
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        return RestClient.builder()
                .messageConverters(converters -> {
                    // ВАЖНО: Добавляем наш конвертер в НАЧАЛО списка (index 0),
                    // чтобы он использовался вместо стандартного.
                    converters.add(0, converter);
                });
    }
}