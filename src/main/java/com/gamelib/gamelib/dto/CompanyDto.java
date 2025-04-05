package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import java.util.ArrayList;
import java.util.List;

public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private Integer foundedYear;
    private String website;
    private List<GameDto> games = new ArrayList<>();

    public CompanyDto() {
    }

    public CompanyDto(Long id, String name, String description, Integer foundedYear,
                      String website) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.foundedYear = foundedYear;
        this.website = website;
    }

    // Конструктор для преобразования из сущности Company
    public CompanyDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.description = company.getDescription();
        this.foundedYear = company.getFoundedYear();
        this.website = company.getWebsite();
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<GameDto> getGames() {
        return games;
    }

    public void setGames(List<GameDto> games) {
        this.games = games;
    }
}