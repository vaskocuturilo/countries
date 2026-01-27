package com.example.apiconsumer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CountryEntity {

    @Id
    String alpha2;
    String alpha3;
    String capital;
}
