package utils;


import com.example.apiconsumer.dto.CountryDto;

import java.util.List;


public class DataUtils {
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
