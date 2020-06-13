package com.abhi.weather.controller;

import java.time.LocalDate;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.abhi.weather.controller.service.WeatherInformationService;
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
	
	@DeleteMapping("/erase/startDate/{startDate}/endDate/{endDate}/latitude/{latitude}/longitude/{longitude}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteByCondition( @PathVariable("startDate") LocalDate startDate, 
			@PathVariable("endDate") LocalDate endDate, @PathVariable("latitude") Float latitude, 
			@PathVariable("longitude") Float longitude ){
		weatherInformationService.deleteWeatherInformationByCondition(startDate, endDate, latitude, longitude);
	}
	
}
