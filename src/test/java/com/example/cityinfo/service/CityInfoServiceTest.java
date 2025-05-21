package com.example.cityinfo.service;

import com.example.cityinfo.config.ApiKeysConfig;
import com.example.cityinfo.dto.CityInfoDTO;
import com.example.cityinfo.dto.external.NewsApiResponse;
import com.example.cityinfo.dto.external.OpenWeatherResponse;
import com.example.cityinfo.dto.external.RestCountriesResponse;
import com.example.cityinfo.entity.CityInfo;
import com.example.cityinfo.repository.CityInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityInfoServiceTest {

    @Mock
    private ApiKeysConfig apiKeysConfig;

    @Mock
    private CityInfoRepository cityInfoRepository;

    @Mock
    private WebClient openWeatherClient;

    @Mock
    private WebClient restCountriesClient;

    @Mock
    private WebClient newsApiClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    private CityInfoService cityInfoService;

    private static final String CITY = "Berlin";

    @BeforeEach
    void setUp() {
        cityInfoService = new CityInfoService(apiKeysConfig, openWeatherClient, restCountriesClient, newsApiClient, cityInfoRepository);
    }

    @Test
    void testGetCityInfoFromCache() {

        CityInfo cachedInfo = new CityInfo();
        cachedInfo.setCity(CITY);
        cachedInfo.setTemperature(23.0);
        cachedInfo.setTopNewsHeadlines(List.of("Berlin wins the green award"));
        cachedInfo.setLastUpdated(LocalDateTime.now());

        when(cityInfoRepository.findByCityOrderByLastUpdatedDesc(CITY)).thenReturn(List.of(cachedInfo));

        Flux<CityInfoDTO> resultFlux = cityInfoService.getCityInfo(List.of(CITY), false);
        List<CityInfoDTO> resultList = resultFlux.collectList().block();

        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        CityInfoDTO dto = resultList.get(0);
        assertEquals(CITY, dto.getCity());
        assertEquals(23.0, dto.getTemperature());
        assertEquals(List.of("Berlin wins the green award"), dto.getTopNewsHeadlines());

        verify(cityInfoRepository).findByCityOrderByLastUpdatedDesc(CITY);
        verifyNoMoreInteractions(cityInfoRepository);

        verifyNoInteractions(openWeatherClient, restCountriesClient, newsApiClient);
    }

    @Test
    void testFetchWeatherAndSave() {

        when(cityInfoRepository.findByCityOrderByLastUpdatedDesc(CITY)).thenReturn(List.of());

        when(openWeatherClient.get()).thenReturn(requestHeadersUriSpec);
        when(restCountriesClient.get()).thenReturn(requestHeadersUriSpec);
        when(newsApiClient.get()).thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), (Object[]) any())).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        OpenWeatherResponse weatherResponse = new OpenWeatherResponse();
        OpenWeatherResponse.Sys sys = new OpenWeatherResponse.Sys();
        sys.setCountry("DE");
        weatherResponse.setSys(sys);
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(20.5);
        weatherResponse.setMain(main);

        when(responseSpec.bodyToMono(OpenWeatherResponse.class)).thenReturn(Mono.just(weatherResponse));

        RestCountriesResponse countryResponse = new RestCountriesResponse();
        countryResponse.setCca2("DE");
        countryResponse.setLanguages(java.util.Map.of("deu", "German"));
        countryResponse.setBorders(List.of("FR", "PL"));

        when(responseSpec.bodyToMono(RestCountriesResponse[].class)).thenReturn(Mono.just(new RestCountriesResponse[]{countryResponse}));

        NewsApiResponse newsResponse = new NewsApiResponse();
        NewsApiResponse.Article article = new NewsApiResponse.Article();
        article.setTitle("Berlin is awesome");
        newsResponse.setArticles(List.of(article));

        when(responseSpec.bodyToMono(NewsApiResponse.class)).thenReturn(Mono.just(newsResponse));

        when(cityInfoRepository.save(any(CityInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<CityInfoDTO> resultList = cityInfoService.getCityInfo(List.of(CITY), true).collectList().block();

        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        CityInfoDTO dto = resultList.get(0);
        assertEquals(CITY, dto.getCity());
        assertEquals(20.5, dto.getTemperature());
        assertEquals("DE", dto.getCountryCode());
        assertEquals("German", dto.getLanguage());
        assertEquals(List.of("FR", "PL"), dto.getBorderingCountries());
        assertEquals(List.of("Berlin is awesome"), dto.getTopNewsHeadlines());

        verify(cityInfoRepository).save(any(CityInfo.class));
    }
}
