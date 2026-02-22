package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WeatherDTO (
        Integer  temp,
        Integer  feelsLike,
        Integer humidity,
        Double speed,
        String main,
        String description,
        String country
){}
