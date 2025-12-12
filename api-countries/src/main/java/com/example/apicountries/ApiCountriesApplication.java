package com.example.apicountries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableMongoRepositories
public class ApiCountriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiCountriesApplication.class, args);
    }

}
