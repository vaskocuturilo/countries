## Build stage
FROM gradle:jdk-21-and-24 AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle clean build -x test --no-daemon --refresh-dependencies || return 0

## Package stage
FROM openjdk:24-ea-1-jdk-slim

ENV port 8080
ENV datasource.url 'jdbc:postgresql://postgres/countries'
ENV datasource.user 'postgres'
ENV datasource.password 'postgres'
ENV mongo.host 'mongo'
ENV mongo.port '27017'
ENV mongo.database 'mongo'

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/api-countries-1.0.0.jar

ENTRYPOINT ["java","-jar","/app/api-countries-1.0.0.jar"]
