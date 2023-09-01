package zai.weather;

import java.time.Duration;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import zai.weather.domain.Error;
import zai.weather.domain.Weather;
import zai.weather.domain.WeatherResult;

@Component
public class CachedProvider implements Provider {

    private Logger logger = LoggerFactory.getLogger(CachedProvider.class);
    private Weather weather;

    // The classes below are package level visiblity for testing reasons
    LocalTime time;
    @Value( "${cachedprovider.caching_interval_secs}" )
    int cachingIntervalInSec;

    @Override
    public WeatherResult getWeather(String city, String country) {
        if ((this.time == null) || 
                (Duration.between(this.time, LocalTime.now()).toMillis() > cachingIntervalInSec * 1000)) {
            logger.info("Cache miss");
            return new WeatherResult(weather, new Error("Stale"));
        } else {
            logger.info("Cache hit");
            return new WeatherResult(weather, null);
        }
    }

    public void cache(Weather weather) {
        if (this.weather != weather) {
            logger.info("Cache refresh");
            this.weather = weather;
            this.time = LocalTime.now();
        }
    }

}
