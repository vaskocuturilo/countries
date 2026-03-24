package utils;

import com.example.apigateway.dto.CountryDto;
import com.example.apigateway.dto.CredentialsDto;
import com.example.apigateway.dto.SignUpDto;
import com.example.apigateway.dto.UserDto;

import java.util.List;


public class DataUtils {

    public static CountryDto simpleCountryBuilder() {
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

    public static SignUpDto simpleSignBuilder() {
        return SignUpDto.builder()
                .firstName("Test1")
                .lastName("Test2")
                .login("login1")
                .build();
    }

    public static UserDto simpleUserBuilder() {
        return UserDto.builder()
                .id(1)
                .firstName("Test1")
                .lastName("Test2")
                .login("login1")
                .active(true)
                .build();
    }

    public static CredentialsDto simpleCredentialBuilder() {
        return CredentialsDto.builder()
                .login("login1")
                .password(new char[]{123})
                .build();
    }
}
