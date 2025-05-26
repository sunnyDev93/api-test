package com.example.cityinfo.controller;

import com.example.cityinfo.dto.CityInfoDTO;
import com.example.cityinfo.service.CityInfoService;
import com.opencsv.CSVWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "City Info", description = "Fetches city data like weather, country, and top news")
@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityInfoController {

    private final CityInfoService cityInfoService;

    @Operation(
        summary = "Get city information",
        description = "Returns weather, language, bordering countries, and top news for each city."
    )
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public Mono<ResponseEntity<?>> getCitiesInfo(
        @Parameter(description = "List of city names", required = true)
        @RequestParam List<String> cities,

        @Parameter(description = "Format to export the data (JSON or CSV)", example = "JSON")
        @RequestParam(defaultValue = "JSON") String exportFormat,

        @Parameter(description = "Whether to include the response body", example = "true")
        @RequestParam(defaultValue = "true") boolean includeResponseBody,

        @Parameter(description = "Whether to save the data in DB", example = "false")
        @RequestParam(defaultValue = "false") boolean saveToDb
    ) {
        if (cities == null || cities.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of(
                "error", "Cities list cannot be empty"
            )));
        }

        return cityInfoService.getCityInfo(cities, saveToDb)
                .collectList()
                .map(cityInfos -> {
                    if (cityInfos == null || cityInfos.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .body("No data found for requested cities");
                    }

                    if (!includeResponseBody) {
                        return ResponseEntity.ok("Data processed and saved successfully");
                    }

                    if ("CSV".equalsIgnoreCase(exportFormat)) {
                        StringWriter writer = new StringWriter();
                        try (CSVWriter csvWriter = new CSVWriter(writer)) {
                            csvWriter.writeNext(new String[] {
                                    "City", "Temperature", "CountryCode", "Language", "BorderingCountries", "TopNewsHeadlines"
                            });

                            for (CityInfoDTO dto : cityInfos) {
                                csvWriter.writeNext(new String[] {
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
