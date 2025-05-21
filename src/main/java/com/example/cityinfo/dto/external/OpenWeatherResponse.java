package com.example.cityinfo.dto.external;

import lombok.Data;

@Data
public class OpenWeatherResponse {
    private Main main;
    private Sys sys;

    @Data
    public static class Main {
        private Double temp;
    }

    @Data
    public static class Sys {
        private String country;
    }
}
