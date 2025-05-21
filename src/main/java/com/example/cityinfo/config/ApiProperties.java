package com.example.cityinfo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api")
@Data
public class ApiProperties {
    private String openweathermapKey;
    private String newsapiKey;

    private String openweathermapBaseUrl = "https://api.openweathermap.org/data/2.5/weather";
    private String restcountriesBaseUrl = "https://restcountries.com/v3.1/name";
    private String newsApiBaseUrl = "https://newsapi.org/v2/top-headlines";
}
