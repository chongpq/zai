package zai.weather;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import zai.weather.domain.Error;
import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;
import zai.weather.openweathermap.OpenWeatherMapClient;
import zai.weather.weatherstack.WeatherStackClient;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

	WeatherResult success = new WeatherResult(new Weather(26.0, 20.0), null);
	WeatherResult error = new WeatherResult(null, new Error("Error"));

	@Autowired
	MockMvc mockMvc;

	@MockBean
	CachedProvider cachedProvider;

	@MockBean
	WeatherStackClient primaryProvider;

	@MockBean
	OpenWeatherMapClient secondaryProvider;

	@MockBean
	RestTemplate restTemplate;

	@Test
	void primaryProviderResult() throws Exception {
		when(cachedProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(primaryProvider.getWeather(anyString(), anyString())).thenReturn(success);
		mockMvc.perform(get("/v1/weather?city=melbourne"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.wind_speed").value(26.0))
			.andExpect(jsonPath("$.temperature_degrees").value(20.0));
	}

	@Test
	void secondaryProviderResult() throws Exception {
		when(cachedProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(primaryProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(secondaryProvider.getWeather(anyString(), anyString())).thenReturn(success);
		mockMvc.perform(get("/v1/weather?city=melbourne"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.wind_speed").value(26.0))
			.andExpect(jsonPath("$.temperature_degrees").value(20.0));
	}

	@Test
	void initialCachedProviderResult() throws Exception {
		when(cachedProvider.getWeather(anyString(), anyString())).thenReturn(success);
		mockMvc.perform(get("/v1/weather?city=melbourne"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.wind_speed").value(26.0))
			.andExpect(jsonPath("$.temperature_degrees").value(20.0));
	}

	@Test
	void secondaryCachedProvider5xxResult() throws Exception {
		when(cachedProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(primaryProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(secondaryProvider.getWeather(anyString(), anyString())).thenReturn(error);
		mockMvc.perform(get("/v1/weather?city=melbourne"))
			.andDo(print())
			.andExpect(status().is5xxServerError());
	}

	@Test
	void secondaryCachedProviderResult() throws Exception {
		when(cachedProvider.getWeather(anyString(), anyString())).thenReturn(new WeatherResult(new Weather(26.0, 20.0), new Error("Stale")));
		when(primaryProvider.getWeather(anyString(), anyString())).thenReturn(error);
		when(secondaryProvider.getWeather(anyString(), anyString())).thenReturn(error);
		mockMvc.perform(get("/v1/weather?city=melbourne"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.wind_speed").value(26.0))
			.andExpect(jsonPath("$.temperature_degrees").value(20.0));
		}
}
