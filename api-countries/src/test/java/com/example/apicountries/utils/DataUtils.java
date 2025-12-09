package com.example.apicountries.utils;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;

import java.util.List;


public class DataUtils {

    public static CountryEntity getTuvaluTransient() {
        return CountryEntity
                .builder()
                .alpha2("TU")
                .alpha3("TUV")
                .capital("Funafuti")
                .build();
    }

    public static CountryDocument getTuvaluMongoTransient() {
        return CountryDocument
                .builder()
                .alpha2("TU")
                .alpha3("TUV")
                .capital("Funafuti")
                .build();
    }

    public static CountryEntity getTuvaluPersisted() {
        return CountryEntity
                .builder()
                .alpha2("TU")
                .alpha3("TUV")
                .capital("Funafuti")
                .build();
    }

    public static CountryDto getTuvaluDtoTransient() {
        return CountryDto
                .builder()
                .alpha2("TU")
                .alpha3("TUV")
                .capital(List.of("Funafuti"))
                .region("Oceania")
                .subregion("Polynesia")
                .area(26)
                .population(10643)
                .build();
    }

    public static CountryDto getTuvaluDtoPersisted() {
        return CountryDto
                .builder()
                .alpha2("TU")
                .alpha3("TUV")
                .capital(List.of("Funafuti"))
                .region("Oceania")
                .subregion("Polynesia")
                .area(26)
                .population(10643)
                .build();
    }
}
