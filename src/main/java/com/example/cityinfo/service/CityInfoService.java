package com.example.cityinfo.service;

import com.example.cityinfo.config.ApiKeysConfig;
import com.example.cityinfo.dto.CityInfoDTO;
import com.example.cityinfo.dto.external.NewsApiResponse;
import com.example.cityinfo.dto.external.OpenWeatherResponse;
import com.example.cityinfo.dto.external.RestCountriesResponse;
import com.example.cityinfo.entity.CityInfo;
import com.example.cityinfo.repository.CityInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityInfoService {

    private final ApiKeysConfig apiKeysConfig;
    private final WebClient openWeatherClient;
    private final WebClient restCountriesClient;
    private final WebClient newsApiClient;
    private final CityInfoRepository cityInfoRepository;

    private final Duration cacheDuration = Duration.ofHours(1);

    public Flux<CityInfoDTO> getCityInfo(List<String> cities, boolean saveToDb) {
        return Flux.fromIterable(cities)
                .flatMap(city -> getOrUpdateCityInfo(city, saveToDb));
    }

    private Mono<CityInfoDTO> getOrUpdateCityInfo(String city, boolean saveToDb) {
        Optional<CityInfo> cachedOpt = getLatestCityInfo(city);

        boolean validCache = cachedOpt.isPresent() && cachedOpt.get().getLastUpdated() != null &&
                cachedOpt.get().getLastUpdated().isAfter(LocalDateTime.now().minus(cacheDuration));

        if (validCache) {
            return Mono.just(toDTO(cachedOpt.get()));
        }

        return fetchCityDataAsync(city)
                .doOnNext(dto -> {
                    if (saveToDb) {
                        saveCityInfo(dto);
                        log.info("Saved fresh data for city: {}", city);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error fetching data for city {}: {}", city, e.toString());
                    return cachedOpt.map(cached -> Mono.just(toDTO(cached))).orElseGet(() -> Mono.error(e));
                });
    }

    private Optional<CityInfo> getLatestCityInfo(String city) {
        List<CityInfo> cityInfos = cityInfoRepository.findByCityOrderByLastUpdatedDesc(city);
        return cityInfos.stream().findFirst();
    }

    private Mono<CityInfoDTO> fetchCityDataAsync(String city) {
        Mono<OpenWeatherResponse> weatherMono = fetchWeather(city)
                .onErrorResume(e -> {
                    log.error("OpenWeatherMap API error for city {}: {}", city, e.toString());
                    return Mono.error(new RuntimeException("Failed to fetch weather data for city: " + city, e));
                });

        Mono<RestCountriesResponse> countryMono = weatherMono.flatMap(weather ->
                fetchCountryByAlphaCode(weather.getSys().getCountry())
                        .onErrorResume(e -> {
                            log.error("REST Countries API error for code {}: {}", weather.getSys().getCountry(), e.toString());
                            return Mono.error(new RuntimeException("Failed to fetch country data for code: " + weather.getSys().getCountry(), e));
                        })
        );

        Mono<NewsApiResponse> newsMono = countryMono.flatMap(country ->
                fetchNews(country.getCca2().toLowerCase())
                        .onErrorResume(e -> {
                            log.error("News API error for country {}: {}", country.getCca2(), e.toString());
                            return Mono.error(new RuntimeException("Failed to fetch news for country: " + country.getCca2(), e));
                        })
        );

        return Mono.zip(weatherMono, countryMono, newsMono)
                .map(tuple -> {
                    OpenWeatherResponse weather = tuple.getT1();
                    RestCountriesResponse country = tuple.getT2();
                    NewsApiResponse news = tuple.getT3();

                    if (weather == null || country == null) {
                        throw new RuntimeException("Essential data is missing for city: " + city);
                    }

                    String language = "N/A";
                    if (country.getLanguages() != null && !country.getLanguages().isEmpty()) {
                        language = country.getLanguages().values().iterator().next();
                    }

                    List<String> borders = country.getBorders() != null ? country.getBorders() : Collections.emptyList();
                    List<String> headlines = (news != null && news.getArticles() != null)
                            ? news.getArticles().stream().map(NewsApiResponse.Article::getTitle).collect(Collectors.toList())
                            : Collections.emptyList();

                    return CityInfoDTO.builder()
                            .city(city)
                            .temperature(weather.getMain().getTemp())
                            .countryCode(weather.getSys().getCountry())
                            .language(language)
                            .borderingCountries(borders)
                            .topNewsHeadlines(headlines)
                            .build();
                })
                .onErrorResume(e -> {
                    log.error("Failed to fetch full city info for {}: {}", city, e.toString());
                    return Mono.error(new RuntimeException("Error processing city data for " + city, e));
                });
    }

    private Mono<OpenWeatherResponse> fetchWeather(String city) {
        return openWeatherClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKeysConfig.getOpenWeatherMapKey())
                .queryParam("units", "metric")
                .build())
            .retrieve()
            .bodyToMono(OpenWeatherResponse.class);
    }

    private Mono<RestCountriesResponse> fetchCountryByAlphaCode(String alphaCode) {
        return restCountriesClient.get()
            .uri("/alpha/{code}", alphaCode)
            .retrieve()
            .bodyToMono(RestCountriesResponse[].class)
            .map(array -> array.length > 0 ? array[0] : null);
    }

    private Mono<NewsApiResponse> fetchNews(String countryCode) {
        return newsApiClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/top-headlines")
                .queryParam("country", countryCode)
                .queryParam("apiKey", apiKeysConfig.getNewsApiKey())
                .build())
            .retrieve()
            .bodyToMono(NewsApiResponse.class);
    }

    private void saveCityInfo(CityInfoDTO dto) {
        CityInfo entity = new CityInfo();
        entity.setCity(dto.getCity());
        entity.setTemperature(dto.getTemperature());
        entity.setCountryCode(dto.getCountryCode());
        entity.setLanguage(dto.getLanguage());
        entity.setBorderingCountries(dto.getBorderingCountries());
        entity.setTopNewsHeadlines(dto.getTopNewsHeadlines());
        entity.setLastUpdated(LocalDateTime.now());

        cityInfoRepository.save(entity);
    }

    private CityInfoDTO toDTO(CityInfo entity) {
        return CityInfoDTO.builder()
                .city(entity.getCity())
                .temperature(entity.getTemperature())
                .countryCode(entity.getCountryCode())
                .language(entity.getLanguage())
                .borderingCountries(entity.getBorderingCountries())
                .topNewsHeadlines(entity.getTopNewsHeadlines())
                .build();
    }
}
