package com.example.cityinfo.config;

import com.example.cityinfo.config.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public WebClient openWeatherClient(ApiKeysConfig apiKeysConfig) {
        return WebClient.builder()
                .baseUrl(ApiConstants.OPENWEATHERMAP_URL)
                .build();
    }

    @Bean
    public WebClient restCountriesClient() {
        return WebClient.builder()
                .baseUrl(ApiConstants.RESTCOUNTRIES_URL)
                .build();
    }

    @Bean
    public WebClient newsApiClient() {
        return WebClient.builder()
                .baseUrl(ApiConstants.NEWSAPI_URL)
                .build();
    }
}
