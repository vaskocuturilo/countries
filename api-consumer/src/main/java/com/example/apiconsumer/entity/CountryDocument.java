package com.example.apiconsumer.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CountryDocument {

    @Id
    String alpha2;
    String alpha3;
    String capital;
    String region;
    String subregion;
    Integer area;
    Integer population;
    Boolean independent;
}
