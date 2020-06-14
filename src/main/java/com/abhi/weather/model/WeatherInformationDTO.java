package com.abhi.weather.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherInformationDTO {

	private Long id;
	private LocalDate date;
	private LocationDTO location;
	private Float[] temprature;
	
	public WeatherInformationDTO() {
	}
	
}
