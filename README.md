# Zai
Weather app

## Run Instructions

Assuming you are executing on a POSIX box, Windows machines are very similar.
* Install [Java 17](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Git clone this repository ```git clone https://github.com/chongpq/zai.git```
* Go to new zai folder ```cd zai```
* Run the gradle command ```./gradlew clean build```
* Run the java command ```java -jar ./build/libs/weather-0.0.1-SNAPSHOT.jar```

## Assumptions
* Assume returning floating point number is ok. This became neccessary since we need to convert m/s to km/h and also certain providers return floating point numbers too.
* The service can hard-code Melbourne as a city. I've coded it so it could potentially use the request params, however using the request params would introduce lots of requirements to validate that input and there is the outlying question of how we can get the country for this problem.

## Design
This is designed as a single GET endpoint which became the WeatherController.

The following 3 specs informed the body of the WeatherController
* If one of the providers goes down, your service can quickly failover to a different provider without affecting your customers.
* Weather results are fine to be cached for up to 3 seconds on the server in normal behaviour to prevent hitting weather providers.
* Cached results should be served if all weather providers are down.

Each provider is encapsulated in its own package: openweathermap, weatherstack. The interface Provider states what they need to provide to integrate with the weather app. In order to avoid multiple nested try catch blocks in the contoller we avoid using exceptions in the Provider interface.

The WeatherResult object in the domain package is similar to golang function returning mutliple results. WeatherResult could have used an ```Either``` object but in order to keep the dependencies low we just used standard Spring Boot.