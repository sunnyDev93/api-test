package com.example.cityinfo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CityInfoDTO {
    private String city;
    private Double temperature;
    private String countryCode;
    private String language;
    private List<String> borderingCountries;
    private List<String> topNewsHeadlines;
    private String error;
}
