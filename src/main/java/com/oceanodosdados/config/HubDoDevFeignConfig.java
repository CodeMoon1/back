package com.oceanodosdados.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;

@Configuration
public class HubDoDevFeignConfig {

    @Value("${api.token}")
    private String apiToken;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.query("token", apiToken);
        };
    }
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            return new RuntimeException("Erro HTTP " + response.status() + " em " + methodKey);
        };
    }
}