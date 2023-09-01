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
* The service can hard-code Melbourne as a city. I've coded it so it could potentially use the request params, however using the request params would introduce lots of requirements to validate that input and there is the underlying question of how we can get the country for this problem.

## Design
This is designed as a single GET endpoint which is the WeatherController.

The following 3 specs informed the body of the WeatherController
* If one of the providers goes down, your service can quickly failover to a different provider without affecting your customers.
* Weather results are fine to be cached for up to 3 seconds on the server in normal behaviour to prevent hitting weather providers.
* Cached results should be served if all weather providers are down.

Each provider is encapsulated in its own package: openweathermap, weatherstack. The interface Provider states what they need to provide to integrate with the weather app. In order to avoid multiple nested try catch blocks in the contoller we avoided using exceptions in the Provider interface. This interface and encapsulation is mean't to keep provider as distinct as possible, making them a future pivot point where we could swap in new provider with minimal changes.

The WeatherResult object in the domain package is similar to golang function returning mutliple results. WeatherResult could have used an ```Either``` object but in order to keep the dependencies low we just used standard Spring Boot.

The use of Spring Boot is to satisfy this spec: ```Have scalability and reliability in mind when designing the solution```. Spring Boot is a battle tested framework and has scaled and run reliably. 

The only potential scaling bottleneck in the code is CachedProvider. Otherwise, I reckon the scaling issues will lie with the providers and not with this code. CachedProvider is a custom solution, I'd prefer it if we used Redis for caching in production or at least a concurrent hashmap solution especially when we tackle multiple city/country in the app.