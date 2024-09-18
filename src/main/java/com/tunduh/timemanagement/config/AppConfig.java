package com.tunduh.timemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public ExecutorService executorService() {return Executors.newFixedThreadPool(5); }
}
