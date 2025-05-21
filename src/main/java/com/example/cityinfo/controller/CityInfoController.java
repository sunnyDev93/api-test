package com.example.cityinfo.controller;

import com.example.cityinfo.dto.CityInfoDTO;
import com.example.cityinfo.service.CityInfoService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityInfoController {

    private final CityInfoService cityInfoService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public Mono<ResponseEntity<?>> getCitiesInfo(
            @RequestParam List<String> cities,
            @RequestParam(defaultValue = "JSON") String exportFormat,
            @RequestParam(defaultValue = "true") boolean includeResponseBody,
            @RequestParam(defaultValue = "true") boolean saveToDb
    ) {
        return cityInfoService.getCityInfo(cities, saveToDb)
                .collectList()
                .map(cityInfos -> {
                    if (cityInfos == null || cityInfos.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No data found for requested cities");
                    }

                    if (!includeResponseBody) {
                        return ResponseEntity.ok("Data processed and saved successfully");
                    }

                    if ("CSV".equalsIgnoreCase(exportFormat)) {
                        StringWriter writer = new StringWriter();
                        try (CSVWriter csvWriter = new CSVWriter(writer)) {
                            csvWriter.writeNext(new String[]{
                                    "City", "Temperature", "CountryCode", "Language", "BorderingCountries", "TopNewsHeadlines"
                            });

                            for (CityInfoDTO dto : cityInfos) {
                                csvWriter.writeNext(new String[]{
                                        dto.getCity(),
                                        String.valueOf(dto.getTemperature()),
                                        dto.getCountryCode(),
                                        dto.getLanguage(),
                                        String.join("|", dto.getBorderingCountries()),
                                        String.join("|", dto.getTopNewsHeadlines())
                                });
                            }
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Failed to generate CSV");
                        }
                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cityinfo.csv\"")
                                .contentType(MediaType.parseMediaType("text/csv"))
                                .body(writer.toString());
                    }

                    return ResponseEntity.ok(cityInfos);
                })
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of(
                                        "error", "Internal Server Error",
                                        "message", e.getMessage(),
                                        "details", e.toString()
                                ))
                ));
    }
}
