package com.abhi.weather.controller.service;

import java.time.LocalDate;
import java.util.List;

import com.abhi.weather.model.WeatherInformationDTO;

public interface WeatherInformationService {

	public Long createWeatherInformation( WeatherInformationDTO weatherInformationDto );
	
	public List<WeatherInformationDTO> findAllWeatherInformation();
	
	public void eraseAllWeatherInformation();
	
	void deleteWeatherInformationByCondition( LocalDate startDate, LocalDate endDate, Float latitude, Float longitude );
}
