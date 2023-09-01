package zai.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;

public class CachedProviderTest {
    
    CachedProvider cachedProvider;

    @BeforeEach
    void setup() {
        cachedProvider = new CachedProvider();
    }

    @Test
    void testColdStartup() {
        WeatherResult wr = cachedProvider.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertEquals("Stale", wr.error().message());
        assertNull(wr.weather());

        Weather w = new Weather(26.0, 20.0);
        cachedProvider.cache(w);

        WeatherResult c = cachedProvider.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNull(c.error());
        assertEquals(w, c.weather());
    }

    @Test
    void testCacheRefreshing() throws InterruptedException {
        Weather w = new Weather(26.0, 20.0);
        cachedProvider.cache(w);

        WeatherResult c = cachedProvider.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNull(c.error());
        assertEquals(w, c.weather());

        TimeUnit.SECONDS.sleep(1);
        c = cachedProvider.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertNull(c.error());
        assertEquals(w, c.weather());

        TimeUnit.SECONDS.sleep(3);
        c = cachedProvider.getWeather(WeatherController.CITY, WeatherController.COUNTRY);
        assertEquals("Stale", c.error().message());
    }

    @Test
    void testNotUpdateingCacheTimeWithSameObject() {
        Weather w = new Weather(26.0, 20.0);
        cachedProvider.cache(w);
        LocalTime expectedTime = cachedProvider.time;

        cachedProvider.cache(w);
        assertEquals(expectedTime, cachedProvider.time);
    }

    @Test
    void testUpdateingCacheWithDiffObject() {
        Weather w = new Weather(26.0, 20.0);
        cachedProvider.cache(w);
        LocalTime time = cachedProvider.time;

        cachedProvider.cache(new Weather(26.0, 20.0));
        assertNotEquals(time, cachedProvider.time);
    }

}
