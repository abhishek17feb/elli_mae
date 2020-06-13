package com.abhi.weather.controller.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhi.weather.controller.domain.Weather;
import com.abhi.weather.controller.repository.WeatherRepository;
import com.abhi.weather.model.LocationDTO;
import com.abhi.weather.model.WeatherInformationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherInformationServiceImpl implements WeatherInformationService {

	@Autowired
	private WeatherRepository weatherRepository;
	
	@Override
	@Transactional
	public Long createWeatherInformation(WeatherInformationDTO weatherInformationDto) {
		
		Weather weather = new Weather();
		try {
			populateWeatherEntity( weather, weatherInformationDto );
			weatherRepository.save(weather);
		} catch (JsonProcessingException e) {
			log.debug("Unable to save weather information ["+ e.getMessage() +"]" );
		}
		
		return weather.getId();
	}

	private void populateWeatherEntity(Weather weather, WeatherInformationDTO weatherInformationDto) throws JsonProcessingException {
		
		weather.setDateRecorded(weatherInformationDto.getDate());
		weather.setLocation(getLocationJson(weatherInformationDto));
		weather.setTemprature(getTempratureJson(weatherInformationDto));
		
	}
	
	private String getLocationJson( WeatherInformationDTO weatherInformationDto ) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(weatherInformationDto.getLocation());
	}
	
	private String getTempratureJson( WeatherInformationDTO weatherInformationDto ) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(weatherInformationDto.getTemprature());
	}
	
	private LocationDTO getLocationDTO( String locationJson ) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(locationJson, LocationDTO.class);
	}
	
	private Float[] getTempratureDTO( String tempratureJson ) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(tempratureJson, Float[].class);
	}

	@Override
	@Transactional
	public List<WeatherInformationDTO> findAllWeatherInformation() {
		List<Weather> weatherInformation = weatherRepository.findAll();
		List<WeatherInformationDTO> weatherInformationDTO = new ArrayList<WeatherInformationDTO>();
		if( weatherInformation != null 
				&& weatherInformation.size() > 0) {
			weatherInformation.forEach( weather -> {
				WeatherInformationDTO dto = new WeatherInformationDTO();
				try {
					dto.setId(weather.getId());
					dto.setDate(weather.getDateRecorded());
					dto.setLocation(getLocationDTO( weather.getLocation() ));
					dto.setTemprature(getTempratureDTO( weather.getTemprature() ));
				} catch (Exception e) {
					log.debug("Error parsing JSON to Object ["+ e.getMessage() +"]");
				}
				weatherInformationDTO.add(dto);
			});
		}
		return weatherInformationDTO;
	}

	@Override
	@Transactional
	public void eraseAllWeatherInformation() {
		weatherRepository.deleteAll();
		log.debug("Deleted all weather information");
	}

	@Override
	@Transactional
	public void deleteWeatherInformationByCondition(LocalDate startDate, LocalDate endDate, Float latitude,
			Float longitude) {
		List<Weather> weatherInformation = weatherRepository.findAll();
		List<Weather> dateFilterWeatherInformation = new ArrayList<>();
		List<Weather> recordsToBeDeleted = new ArrayList<>();
		if( null != weatherInformation && weatherInformation.size() > 0 ) {
			weatherInformation.forEach( information -> {
				LocalDate recordDate = information.getDateRecorded();
				if( (recordDate.isEqual(startDate) && recordDate.isAfter(startDate)) 
						&& (recordDate.isEqual(endDate) && recordDate.isBefore(endDate))) {
					dateFilterWeatherInformation.add(information);
				}
			});
			
			dateFilterWeatherInformation.forEach( potentialInformationForDeletion -> {
				String location = potentialInformationForDeletion.getLocation();
				try {
					LocationDTO locationDTO = this.getLocationDTO(location);
					if( locationDTO.getLatitude().equals(latitude) && locationDTO.getLongitude().equals(longitude) ) {
						recordsToBeDeleted.add(potentialInformationForDeletion);
					}
				} catch (Exception e) {
					log.debug("Exception parsing");
				}
			});
			
			weatherRepository.deleteAll(recordsToBeDeleted);
		}
	}

}
