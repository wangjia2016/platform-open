package com.platform.ai.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AiWebConfig {
    @Bean
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder, AiProperties props) {
        return builder
                .setConnectTimeout(Duration.ofMillis(Math.min(3000, props.getTimeoutMs())))
                .setReadTimeout(Duration.ofMillis(props.getTimeoutMs()))
                .build();
    }
}
