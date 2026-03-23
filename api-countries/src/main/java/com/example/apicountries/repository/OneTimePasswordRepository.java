package com.example.apicountries.repository;


import com.example.apicountries.entity.OneTimePasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePasswordEntity, Integer> {

    @Query("SELECT code.oneTimePasswordCode FROM OneTimePasswordEntity code WHERE code.user.id = ?1")
    Integer findByOneTimePasswordCode(final Integer userId);
    @Modifying
    @Query("delete from OneTimePasswordEntity code where code.oneTimePasswordCode = :code")
    void deleteByOneTimePasswordCode(final Integer code);
}
