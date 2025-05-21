package com.example.cityinfo.repository;

import com.example.cityinfo.entity.CityInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityInfoRepository extends JpaRepository<CityInfo, Long> {
    List<CityInfo> findByCityOrderByLastUpdatedDesc(String city);
}
