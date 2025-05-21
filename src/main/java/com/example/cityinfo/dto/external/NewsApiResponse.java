package com.example.cityinfo.dto.external;

import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {
    private List<Article> articles;

    @Data
    public static class Article {
        private String title;
    }
}
