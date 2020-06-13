package com.abhi.weather.controller.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="weather")
public class Weather {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private LocalDate dateRecorded;
	
	private String location;
	
	private String temprature;
	
}
