package zai.weather.weatherstack;

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

@RestClientTest(WeatherStackClient.class)
public class WeatherStackClientTest {

    @Autowired
    WeatherStackClient weatherStackClient;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testNullReturnFromRestTemplate() {
        this.server.expect(anything()).andRespond(withSuccess());
        WeatherResult wr = weatherStackClient.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNotNull(wr.error());
    }

   
    @Test
    void testWeatherResultReturnFromRestTemplate() throws JsonProcessingException {
        WeatherStackResponse wResponse = new WeatherStackResponse(new Current(26.0, 7.0));
        String str = objectMapper.writeValueAsString(wResponse);
        this.server.expect(anything()).andRespond(withSuccess(str, MediaType.APPLICATION_JSON));
        WeatherResult wr = weatherStackClient.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNotNull(wr.weather());
        assertEquals(wResponse.current().temperature(), wr.weather().temperature_degrees());
        assertEquals(wResponse.current().wind_speed(), wr.weather().wind_speed());
    } 
}
