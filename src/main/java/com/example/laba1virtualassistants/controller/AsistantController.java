package com.example.laba1virtualassistants.controller;

import com.example.laba1virtualassistants.service.AsistantService;
import dto.GeoResponseDTO;
import dto.WeatherDTO;
import dto.WeatherOpenAPIResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class AsistantController {
    private final AsistantService asistantService;

    public AsistantController(AsistantService asistantService) {
        this.asistantService = asistantService;
    }

    @GetMapping("/weather")
    public WeatherDTO getWeather (
            @RequestParam String city
    ){
        return asistantService.getWeatherByCity(city) ;
    }


}
