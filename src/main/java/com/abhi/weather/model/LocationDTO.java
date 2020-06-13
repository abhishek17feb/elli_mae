package com.abhi.weather.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private String cityName;
    private String stateName;
    private Float latitude;
    private Float longitude;

}
