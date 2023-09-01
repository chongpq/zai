package zai.weather.weatherstack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Current(Double temperature, Double wind_speed) { }    