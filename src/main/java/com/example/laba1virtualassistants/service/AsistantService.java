package com.example.laba1virtualassistants.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.laba1virtualassistants.dto.GeoResponseDTO;
import com.example.laba1virtualassistants.dto.WeatherDTO;
import com.example.laba1virtualassistants.dto.WeatherOpenAPIResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;



@Service
public class AsistantService {

    private final RestClient restClient;

    @Value("${openweather.api-key}")
    private String apiKey;
    @Value("${openweather.units:metric}")
    private String units;
    @Value ("${openweather.lang:ua}")
    private String lang;
    private static final Logger log = LoggerFactory.getLogger(AsistantService.class);
    public AsistantService(RestClient.Builder resclientBuilder, @Value ("${openweather.base-url}") String baseUrl)
    {
        this.restClient = resclientBuilder.baseUrl(baseUrl).build();
    }

    public GeoResponseDTO getGeoByCity (String city){
        GeoResponseDTO[] geo = restClient.get().uri( uriBuilder -> uriBuilder
                .path("/geo/1.0/direct")
                .queryParam("q", city)
                .queryParam("limit", 1)
                .queryParam("appid", apiKey)
                .build())
                .retrieve()
                .body(GeoResponseDTO[].class);
        if ( geo==null || geo.length==0 ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Місто не знайдено: " + city);
        }
        GeoResponseDTO location = geo[0];
        System.out.println(location);
        return location;
    }


    public WeatherOpenAPIResponse getWeatherByGeo(GeoResponseDTO geo){
        WeatherOpenAPIResponse weather= restClient.get().uri(uriBuilder -> uriBuilder
                .path("/data/2.5/weather").queryParam("lat", geo.lat().toString())
                .queryParam("lon", geo.lon().toString())
                .queryParam("appid", apiKey)
                .queryParam("units", units)
                .queryParam("lang", lang).build()).retrieve().body(WeatherOpenAPIResponse.class);
            return weather;
    }


    @Cacheable(cacheNames = "weatherDtoByCity",
            key = "#city.trim().toLowerCase()",
            unless = "#result == null"
    )
    public WeatherDTO getWeatherByCity(String city){
        try{


        GeoResponseDTO geo = getGeoByCity(city);
        WeatherOpenAPIResponse weatherResponse = getWeatherByGeo(geo);
        String main = null;
        String description = null;
        if (weatherResponse.weather() != null && !weatherResponse.weather().isEmpty()) {
            main = weatherResponse.weather().get(0).main();
            description = weatherResponse.weather().get(0).description();
        }

        WeatherDTO weather = new WeatherDTO(
                weatherResponse.main() != null ? weatherResponse.main().temp().intValue() : null,
                weatherResponse.main() != null ? weatherResponse.main().feelsLike().intValue() : null,
                weatherResponse.main() != null ? weatherResponse.main().humidity() : null,
                weatherResponse.wind() != null ? weatherResponse.wind().speed() : null,
                main,
                description,
                weatherResponse.sys() != null ? weatherResponse.sys().country() : null
        );


        return weather;
        } catch (ResponseStatusException e) {
            throw e;
        }
        catch (RestClientException e){
            log.error("OpenWeather Api недоступне для міста '{}': {}",
                    city, e.getMessage(),e);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Сервіс погоди тимчасово недоступний. Спробуйте пізніше"
            );
        } catch (Exception e) {
            log.error("Внутрішня помилка при отриманні погоди для '{}': {}", city, e.getMessage(),e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутрішня помилка сервера"
            );
        }
    }


}