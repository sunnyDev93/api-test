package com.example.cityinfo.dto.external;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RestCountriesResponse {
    private Map<String, String> languages;
    private List<String> borders;
    private String cca2;
}
