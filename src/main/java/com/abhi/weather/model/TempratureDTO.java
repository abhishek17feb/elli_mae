package com.abhi.weather.model;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempratureDTO implements Comparator<TempratureDTO> {

	private Float latitude;
	private Float longitude;
	private String cityName;
	private String stateName;
	private float maxTemprature;
	private float minTemprature;

	@Override
	public int compare(TempratureDTO o1, TempratureDTO o2) {
		int cityCompare = o1.getCityName().compareTo(o2.getCityName());
		int stateCompare = o1.getStateName().compareTo(o2.getStateName());
		if (cityCompare == 0) {
			return ((stateCompare == 0) ? cityCompare : stateCompare);
		} else {
			return cityCompare;
		}
	}
}
