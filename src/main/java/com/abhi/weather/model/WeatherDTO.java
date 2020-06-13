package com.abhi.weather.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDTO {

	private Long id;
	private Date dateRecorded;
	private LocationDTO location;
	private Float[] temperature;
}
