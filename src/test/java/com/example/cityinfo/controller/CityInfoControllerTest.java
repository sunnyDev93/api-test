package com.example.cityinfo.controller;

import com.example.cityinfo.dto.CityInfoDTO;
import com.example.cityinfo.service.CityInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(CityInfoController.class)
public class CityInfoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CityInfoService cityInfoService;

    private static final String LONDON = "London";
    private static final String CITIES = "cities";
    private static final String API_URL = "/api/v1/cities";

    private CityInfoDTO createSampleDto() {
        return CityInfoDTO.builder()
            .city(LONDON)
            .temperature(15.0)
            .countryCode("GB")
            .language("English")
            .borderingCountries(List.of("IE", "FR"))
            .topNewsHeadlines(List.of("Latest news headline"))
            .build();
    }

    @Test
    void testGetCityInfoSuccess() {
        CityInfoDTO dto = createSampleDto();

        Mockito.when(cityInfoService.getCityInfo(List.of(LONDON), true))
               .thenReturn(Flux.just(dto));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(API_URL)
                .queryParam(CITIES, LONDON)
                .queryParam("exportFormat", "JSON")
                .queryParam("includeResponse", "true")
                .queryParam("saveToDb", "true")
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(CityInfoDTO.class)
            .hasSize(1)
            .consumeWith(response -> {
                CityInfoDTO responseDto = response.getResponseBody().get(0);
                assert responseDto.getCity().equals(LONDON);
                assert responseDto.getCountryCode().equals("GB");
            });
    }

    @Test
    void testGetCityInfoServiceThrowsException() {
        Mockito.when(cityInfoService.getCityInfo(List.of(LONDON), true))
               .thenReturn(Flux.error(new RuntimeException("Service failure")));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path(API_URL)
                .queryParam(CITIES, LONDON)
                .queryParam("export", "true")
                .build())
            .exchange()
            .expectStatus().is5xxServerError();
    }
}
