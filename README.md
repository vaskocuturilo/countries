## 
The countries project.

### This project shows how to work with an external API: 

https://restcountries.com/v3.1/all?fields=cca2,cca3,capital,region,subregion,area,population,independent

## Requirements:

- Use any *object-oriented language*.
- Use the latest Spring Boot version.
- Use the latest PostgreSQL database version.
- Use the latest MongoDB database version.
- Use the latest OpenAPI.  
- Create a REST API service.
- Create an API Gateway with Async.
- Add Cash (Redis). 
- Add observability (Prometheus, Grafana, Loki).
- Add Docker and Docker Compose. 
- Add Kubernetes.
- Add unit and integration tests. 
- Provide code and clear instructions on how to run it.

You will need the following technologies available to try it out:

* Git
* Spring Boot 3+
* PostgresSQL 17+
* MongoDB 7.0+
* Gradle 9+
* JDK 24+
* Docker
* Docker compose
* Kubernetes
* Redis
* Prometheus
* Grafana
* Loki
* Watchtower
* IDE of your choice

## TO-DO list:

This is all the tasks on the project page: https://github.com/users/vaskocuturilo/projects/4

### How it works:
 API GATEWAY (React WebFlux) -> REST API (/api/v1/countries) -> External REST API (restcountries)

### How to run via Spring Boot.

``` ./gradlew bootRun ```

###  How to run via Docker Compose.

``` docker-compose up --detach --build```
