package com.example.apicountries.dto;


import com.example.apicountries.entity.CountryDocument;
import com.example.apicountries.entity.CountryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static CountryDto fromJpaEntity(final CountryEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return CountryDto
                .builder()
                .alpha2(entity.getAlpha2())
                .alpha3(entity.getAlpha3())
                .capital(StringUtils.hasText(entity.getCapital()) ? List.of(entity.getCapital()) : null)
                .build();
    }

    public CountryEntity toJpaEntity() {
        return CountryEntity.builder()
                .alpha2(this.getAlpha2())
                .alpha3(this.getAlpha3())
                .capital(CollectionUtils.isEmpty(this.getCapital()) ? null : this.getCapital().getFirst())
                .build();
    }

    public static CountryDto fromMongoDocument(CountryDocument document) {
        if(Objects.isNull(document)) {
            return null;
        }
        return CountryDto.builder()
                .alpha2(document.getAlpha2())
                .alpha3(document.getAlpha3())
                .region(document.getRegion())
                .subregion(document.getSubregion())
                .capital(List.of(document.getCapital()))
                .area(document.getArea())
                .population(document.getPopulation())
                .independent(document.getIndependent())
                .build();
    }

    public CountryDocument toMongoDocument() {
        return CountryDocument.builder()
                .alpha2(this.getAlpha2())
                .alpha3(this.getAlpha3())
                .region(this.getRegion())
                .subregion(this.getSubregion())
                .capital(CollectionUtils.isEmpty(this.getCapital()) ? null : this.getCapital().getFirst())
                .area(this.getArea())
                .population(this.getPopulation())
                .independent(this.getIndependent())
                .build();
    }
}
