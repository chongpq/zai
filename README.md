# Zai
Weather app

## Run Instructions

Assuming you are executing on a POSIX box, Windows machines are very similar.
* Install Java 17
* Git clone this repository ```git clone https://github.com/chongpq/zai.git```
* Go to new zai folder ```cd zai```
* Run the gradle command ```./gradlew clean build```
* Run the java command ```java -jar ./build/libs/weather-0.0.1-SNAPSHOT.jar```

## Assumptions
* Assume returning floating point number is ok. This bacame neccessary since we need to convert m/s to km/h and certain providers also return floating point numbers.
* The service can hard-code Melbourne as a city. I've coded it so it could potentially use the request params, however using the request params would introduce lots of requirements to validate that input and there is the outlying question of how we can get the country for this problem.