package com.example.cityinfo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApiKeysConfig {

    @Value("${api.openweathermap.key}")
    private String openWeatherMapKey;

    @Value("${api.newsapi.key}")
    private String newsApiKey;

}
