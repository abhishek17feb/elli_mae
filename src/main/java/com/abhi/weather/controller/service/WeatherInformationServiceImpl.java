package com.abhi.weather.controller.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.abhi.weather.controller.domain.Weather;
import com.abhi.weather.controller.repository.WeatherRepository;
import com.abhi.weather.model.LocationDTO;
import com.abhi.weather.model.TempratureDTO;
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
				if( (recordDate.isEqual(startDate) || recordDate.isAfter(startDate)) 
						&& (recordDate.isEqual(endDate) || recordDate.isBefore(endDate))) {
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

	@Override
	@Transactional
	public ResponseEntity<List<WeatherInformationDTO>> findByLatAndLonCondition(Float latitude, Float longitude) {
		List<Weather> weatherInformation = weatherRepository.findAllWeatherInformation();
		List<WeatherInformationDTO> response = new ArrayList<>();
		if( null != weatherInformation && weatherInformation.size() > 0 ) {
			weatherInformation.forEach(information -> {
				try {
					LocationDTO locationDTO = this.getLocationDTO(information.getLocation());
					Float lat = locationDTO.getLatitude();
					Float lon = locationDTO.getLongitude();
					
					if( lat.equals(latitude) && lon.equals(longitude) ) {
						WeatherInformationDTO weatherInfoDTO = new WeatherInformationDTO();
						weatherInfoDTO.setDate(information.getDateRecorded());
						weatherInfoDTO.setId(information.getId());
						weatherInfoDTO.setLocation(locationDTO);
						weatherInfoDTO.setTemprature(this.getTempratureDTO(information.getTemprature()));
						response.add(weatherInfoDTO);
					}
					
				} catch (Exception e) {
					log.debug("Error parsing JSON");
				}
			});
		}
		HttpStatus status = response.size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
		ResponseEntity<List<WeatherInformationDTO>> weatherResponse = new ResponseEntity<List<WeatherInformationDTO>>(response, status);	
		return weatherResponse;
	}

	private void populateEntityToDto( Weather weather, TempratureDTO tempratureDTO ) {
		try {
			LocationDTO locationDTO = this.getLocationDTO(weather.getLocation());
			Float[] temprature = this.getTempratureDTO(weather.getTemprature());
			
			tempratureDTO.setCityName(locationDTO.getCityName());
			tempratureDTO.setStateName(locationDTO.getStateName());
			tempratureDTO.setLatitude(locationDTO.getLatitude());
			tempratureDTO.setLongitude(locationDTO.getLongitude());
			tempratureDTO.setMaxTemprature(Collections.max(Arrays.asList(temprature)));
			tempratureDTO.setMinTemprature(Collections.min(Arrays.asList(temprature)));
			
		} catch (Exception e) {
			log.debug("Error parsing json" + e);
		}
	}
	
	@Override
	@Transactional
	public List<TempratureDTO> findMinMaxTemprature(LocalDate startDate, LocalDate endDate) {
		List<Weather> weatherInformation = weatherRepository.findAllWeatherInformation();
		List<TempratureDTO> response = new ArrayList<>();
		if( null != weatherInformation && weatherInformation.size() > 0 ) {
			weatherInformation.forEach(information->{
				TempratureDTO tempratureDto = new TempratureDTO();
				this.populateEntityToDto(information, tempratureDto);
				if( (information.getDateRecorded().isEqual(startDate) || information.getDateRecorded().isAfter(startDate)) 
						&& (information.getDateRecorded().isEqual(endDate) || information.getDateRecorded().isBefore(endDate))) {
					response.add(tempratureDto);
				}
			});
		}
		
		if( response.size() > 0 ) {
			Comparator<TempratureDTO> cityStateNameComparator = new Comparator<TempratureDTO>() {
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
			};
			
			Collections.sort(response, cityStateNameComparator);
		}
		
		return response;
	}

}
