package com.example.apiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {

    @JsonProperty("cca2")
    String alpha2;
    @JsonProperty("cca3")
    String alpha3;
    List<String> capital;
    String region;
    String subregion;
    Integer area;
    Integer population;
    Boolean independent;
}
