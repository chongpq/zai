package zai.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;
import zai.weather.openweathermap.OpenWeatherMapClient;
import zai.weather.weatherstack.WeatherStackClient;

@RestController
public class WeatherController {

    public static final String CITY = "melbourne";
    public static final String COUNTRY = "AU";
    private Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private CachedProvider cachedProvider;
    private WeatherStackClient primaryProvider;
    private OpenWeatherMapClient secondaryProvider;

    public WeatherController(CachedProvider cachedProvider, WeatherStackClient weatherStackClient, OpenWeatherMapClient openWeatherMapClient) {
        this.cachedProvider = cachedProvider;
        this.primaryProvider = weatherStackClient;
        this.secondaryProvider = openWeatherMapClient;
    }

    @GetMapping("/v1/weather")
    public Weather weather(@RequestParam(value = "city") String city) {
        //call cache
        WeatherResult cached = cachedProvider.getWeather(CITY,COUNTRY);
        
        //set wr
        WeatherResult wr = (cached.error() == null) ? cached : primaryProvider.getWeather(CITY, COUNTRY);
        if (wr.error() != null) {
            //is error call secondary provider 
            wr = secondaryProvider.getWeather(CITY, COUNTRY);  
        }
        if (wr.error() != null) {
            //is error return what was in cache
            if (cached.weather() != null) {
                //we have result to return
                wr = cached;
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
            }
        }
        cachedProvider.cache(wr.weather());
    
        return wr.weather();
    }

}