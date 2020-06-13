package com.abhi.weather.controller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhi.weather.controller.domain.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long>{

}
