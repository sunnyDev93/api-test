package com.example.cityinfo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApiKeysConfig {

    @Value("${API_OPENWEATHERMAP_KEY}")
    private String openWeatherMapKey;

    @Value("${API_NEWSAPI_KEY}")
    private String newsApiKey;

}
