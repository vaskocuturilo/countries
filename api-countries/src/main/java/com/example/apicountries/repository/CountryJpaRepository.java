package com.example.apicountries.repository;

import com.example.apicountries.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryJpaRepository extends JpaRepository<CountryEntity, String> {

    CountryEntity findByAlpha2(String alpha2);
}
