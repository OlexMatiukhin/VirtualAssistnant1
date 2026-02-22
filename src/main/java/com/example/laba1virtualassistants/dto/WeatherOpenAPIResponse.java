package com.example.laba1virtualassistants.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WeatherOpenAPIResponse (
        MainDTO main,
        WindDTO wind,
        List<WeatherItemDTO> weather,
        Integer visibility,
        String name,
        SysDTO sys
    ){
        public record MainDTO( Double temp,
                               @JsonProperty("feels_like") Double feelsLike,
                               Integer humidity
                               ){}

        public record WindDTO( Double speed
        ){}

        public record WeatherItemDTO( String main, String description ) {}
        public record SysDTO( String country ) {}

    }


