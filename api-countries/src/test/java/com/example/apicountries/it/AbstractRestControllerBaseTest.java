package com.example.apicountries.it;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class AbstractRestControllerBaseTest {

    @Container
    static final PostgreSQLContainer POSTGRES_SQL_CONTAINER;

    static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:latest")
                .withUsername("postgres")
                .withPassword("password")
                .withDatabaseName("countries_testcontainers");

        POSTGRES_SQL_CONTAINER.start();


        MONGO_DB_CONTAINER = new MongoDBContainer("mongo:latest");

        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SQL_CONTAINER::getPassword);
    }
}
