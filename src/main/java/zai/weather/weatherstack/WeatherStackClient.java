package zai.weather.weatherstack;

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
public class WeatherStackClient implements Provider {

    private Logger logger = LoggerFactory.getLogger(WeatherStackClient.class);
	private RestTemplate restTemplate;
    @Value( "${weatherstack.url}" )
	private String url;
    @Value( "${weatherstack.access_key}" )
	private String accessKey;

	public WeatherStackClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public WeatherResult getWeather(String city, String country) {
		logger.info("Calling WebStack Rest Service");
		try {
			WeatherStackResponse wr = restTemplate.getForObject( String.format(url, accessKey, city, country),
				WeatherStackResponse.class);
			return new WeatherResult(new Weather(wr.current().wind_speed(), wr.current().temperature()), null);
		} catch (RestClientException | NullPointerException e) {
			logger.warn("Exception occured. Putting exception message into error message.", e);
			return new WeatherResult(null, new Error(e.getMessage()));
		}
	}
}