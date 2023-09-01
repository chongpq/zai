package zai.weather.openweathermap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import zai.weather.WeatherController;
import zai.weather.domain.WeatherResult;

@RestClientTest(OpenWeatherMapClient.class)
public class OpenWeatherMapClientTest {

    @Autowired
    OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testNullReturnFromRestTemplate() {
        this.server.expect(anything()).andRespond(withSuccess());
        WeatherResult wr = openWeatherMapClient.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNotNull(wr.error());
    }

    @Test
    void testWeatherResultReturnFromRestTemplate() throws JsonProcessingException {
        OpenWeatherMapResponse or = new OpenWeatherMapResponse(new Main(30.0), new Wind(5.0));
        String os = objectMapper.writeValueAsString(or);
        this.server.expect(anything()).andRespond(withSuccess(os, MediaType.APPLICATION_JSON));
        WeatherResult wr = openWeatherMapClient.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNotNull(wr.weather());
        assertEquals(or.main().temp(), wr.weather().temperature_degrees());
        assertEquals(or.wind().speed() * 3.6, wr.weather().wind_speed());
    }
}
