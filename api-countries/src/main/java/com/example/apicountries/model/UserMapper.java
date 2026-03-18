package com.example.apicountries.model;

import com.example.apicountries.dto.SignUpDto;
import com.example.apicountries.dto.UserActiveDto;
import com.example.apicountries.dto.UserDto;
import com.example.apicountries.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(UserEntity user);

    UserActiveDto toUserActiveDto(UserEntity user);

    @Mapping(target = "password", ignore = true)
    UserEntity signUpToUser(SignUpDto signUpDto);
}
