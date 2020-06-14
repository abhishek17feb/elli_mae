package com.abhi.weather.controller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abhi.weather.controller.domain.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Long>{

	@Query("SELECT weather from Weather weather ORDER BY weather.id ASC")
	List<Weather> findAllWeatherInformation();
	
}
