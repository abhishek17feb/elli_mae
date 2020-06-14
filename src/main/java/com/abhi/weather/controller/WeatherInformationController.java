package com.abhi.weather.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.abhi.weather.controller.service.WeatherInformationService;
import com.abhi.weather.model.TempratureDTO;
import com.abhi.weather.model.WeatherInformationDTO;

@RestController
@RequestMapping("/weather")
public class WeatherInformationController {

	@Autowired
	private WeatherInformationService weatherInformationService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long createWeatherInformation( @RequestBody WeatherInformationDTO weatherInformationDto ) {
		return weatherInformationService.createWeatherInformation(weatherInformationDto);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<WeatherInformationDTO> findAllWeatherInformation(){
		return weatherInformationService.findAllWeatherInformation();
	}
	
	@DeleteMapping("/erase")
	@ResponseStatus(HttpStatus.OK)
	public void deleteAllWeatherInformationData() {
		weatherInformationService.eraseAllWeatherInformation();
	}
	
	@DeleteMapping("/erase/filter")
	@ResponseStatus(HttpStatus.OK)
	public void deleteByCondition( @RequestParam(name = "startDate")
			@DateTimeFormat(iso = ISO.DATE) LocalDate startDate, 
			@RequestParam(name="endDate") @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
			@RequestParam(name="latitude") Float latitude, 
			@RequestParam(name="longitude") Float longitude ){
		weatherInformationService.deleteWeatherInformationByCondition(startDate, endDate, latitude, longitude);
	}
	
	@GetMapping("/filter")
	@ResponseBody
	public ResponseEntity<List<WeatherInformationDTO>> findByCondition( @RequestParam(name="latitude") Float latitude, 
			@RequestParam(name="longitude") Float longitude ){
		return weatherInformationService.findByLatAndLonCondition(latitude, longitude);
	}
	
	@GetMapping("/temprature")
	@ResponseStatus(HttpStatus.OK)
	public List<TempratureDTO> findMinMaxTemprature(
			@RequestParam(name = "startDate") @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate") @DateTimeFormat(iso = ISO.DATE) LocalDate endDate) {
		return weatherInformationService.findMinMaxTemprature(startDate, endDate);
	}
}
