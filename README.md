## 
The demo countries project.

### This project was created to demonstrate how to work with an external API. 
It should cover best practices, microservice patterns, scalability, observability, and failure handling.  

## Requirements:

- Use any *object-oriented language*.
- Use the latest stable Spring Boot version.
- Use the latest stable PostgreSQL database version.
- Use the latest stable MongoDB database version.
- Use the latest OpenAPI.

### 
- Create a REST API service.
- Create the other REST service with Kafka consumer support.
- Create an API Gateway with an async approach.
- Add Apache Kafka.
- Add Kafka UI.
- Add Cash (Redis). 
- Add observability (Prometheus, Grafana, Loki).
- Add Docker and Docker Compose. 
- Add Kubernetes.
- Add unit(repository, service and rest) and integration tests with testcontainers.

### 
- Provide code and clear instructions on how to run it.

You will need the following technologies available to try it out:

* Git
* Spring Boot 3+
* PostgresSQL 17+
* MongoDB 7.0+
* Gradle 9+
* JDK 24+
* Apache Kafka
* Kafbat UI (for Apache Kafka)
* Docker
* Docker compose
* Kubernetes
* Redis
* Prometheus
* Grafana
* Loki
* Watchtower
* IDE of your choice

### How it works:

 API GATEWAY (React WebFlux) 
 -> REST API (/api/v1/countries) -> External REST API (Countries: https://restcountries.com/v3.1/all?fields=cca2,cca3,capital,region,subregion,area,population,independent)
 -> Producer -> Kafka 
 -> Kafka -> REST API (/api/v1/consumer)
### How to run via Spring Boot.

``` ./gradlew bootRun ```

###  How to run via Docker Compose.

``` docker-compose up --detach --build```

### HOW it looks:

![](https://i.postimg.cc/4dHP44jJ/Screenshot-at-Jan-19-17-30-09.png)
