package zai.weather.openweathermap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import zai.weather.Provider;
import zai.weather.domain.Error;
import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;

@Component
public class OpenWeatherMapClient implements Provider {

    private Logger logger = LoggerFactory.getLogger(OpenWeatherMapClient.class);
    private RestTemplate restTemplate;
    @Value( "${openweathermap.appid}" )
    private String appId;
    @Value( "${openweathermap.url}" )
    private String url;

    public OpenWeatherMapClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResult getWeather(String city, String country) {
		logger.info("Calling OpenWeatherMap Rest Service");
        try {
            OpenWeatherMapResponse or = restTemplate.getForObject( String.format(url, city, country, appId),
                OpenWeatherMapResponse.class);
            Double kmPerHour = or.wind().speed() * 3.6;
            return new WeatherResult(new Weather(kmPerHour, or.main().temp()), null);
        } catch(RestClientException | NullPointerException e) {
			logger.warn("Exception occured. Putting exception message into error message.", e);
            return new WeatherResult(null, new Error(e.getMessage()));
        }
    }
    
}

