package com.example.apicountries.repository;

import com.example.apicountries.entity.CountryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryMongoRepository extends MongoRepository<CountryDocument, String> {

    CountryDocument findByAlpha2(String alpha2);
}
