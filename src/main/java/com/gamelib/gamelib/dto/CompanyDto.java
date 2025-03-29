package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;


public class CompanyDto {
    private Long id;
    private String name;
    private String country;
    private Set<String> games; // Названия игр

    // Конструкторы
    public CompanyDto() {
    }

    public CompanyDto(Long id, String name, String country, Set<String> games) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.games = games;
    }

    public CompanyDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.country = company.getCountry();
        this.games = (company.getGames() != null)
                ? company.getGames().stream().map(Game::getTitle).collect(Collectors.toSet())
                : Collections.emptySet(); // Безопасная обработка null
    }



    // Геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Set<String> getGames() {
        return games;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setGames(Set<String> games) {
        this.games = games;
    }
}
