package zai.weather;

import java.time.Duration;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import zai.weather.domain.Error;
import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;

@Component
public class CachedProvider implements Provider {

    Logger logger = LoggerFactory.getLogger(CachedProvider.class);
    Weather weather;
    LocalTime time;

    @Override
    public WeatherResult getWeather(String city, String country) {
        if ((this.time == null) || (Duration.between(this.time, LocalTime.now()).getSeconds() > 3)) {
            logger.info("cache miss");
            return new WeatherResult(weather, new Error("Stale"));
        } else {
            logger.info("cache hit");
            return new WeatherResult(weather, null);
        }
    }

    public void cache(Weather weather) {
        if (this.weather != weather) {
            logger.info("cache refresh");
            this.weather = weather;
            this.time = LocalTime.now();
        }
    }

}
