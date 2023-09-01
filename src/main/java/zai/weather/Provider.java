package zai.weather;

import zai.weather.domain.WeatherResult;

public interface Provider {

    WeatherResult getWeather(String city, String country);

}