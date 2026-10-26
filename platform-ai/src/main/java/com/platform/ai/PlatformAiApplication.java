package com.platform.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.platform.ai.config.AiProperties;

@SpringBootApplication(
        scanBasePackages = {"com.platform.ai"},
        exclude = {
                DataSourceAutoConfiguration.class,
                RedisAutoConfiguration.class,
                RedisRepositoriesAutoConfiguration.class
        }
)
@EnableConfigurationProperties(AiProperties.class)
@EnableDiscoveryClient
public class PlatformAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformAiApplication.class, args);
    }
}
