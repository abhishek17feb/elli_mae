package com.abhi.weather.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherInformationDTO {

	private Long id;
	private LocalDate date;
	private String locationJSON;
	private String tempratureJSON;
	private LocationDTO location;
	private Float[] temprature;
	
	public WeatherInformationDTO() {
	}
	
	public WeatherInformationDTO(Long id, LocalDate date, String locationJSON, String tempratureJSON) {
		this.id = id;
		this.date = date;
		this.locationJSON = locationJSON;
		this.tempratureJSON = tempratureJSON;
	}
	
	
}
