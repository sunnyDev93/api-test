package com.example.cityinfo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "city_info")
@Data
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private Double temperature;

    private String countryCode;

    private String language;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "city_info_borders", joinColumns = @JoinColumn(name = "city_info_id"))
    @Column(name = "bordering_country")
    private List<String> borderingCountries;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "city_info_news", joinColumns = @JoinColumn(name = "city_info_id"))
    @Column(name = "headline")
    private List<String> topNewsHeadlines;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
