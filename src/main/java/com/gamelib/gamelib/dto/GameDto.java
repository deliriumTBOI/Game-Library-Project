package com.gamelib.gamelib.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

public class GameDto {
    private Long id;
    private String title;
    private String description;
    private Date releaseDate;
    private String genre;
    private Set<Long> companyIds = new HashSet<>();
    private List<ReviewDto> reviews = new ArrayList<>();

    public GameDto() {
    }

    public GameDto(Long id, String title, String description, Date releaseDate, String genre) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    // Конструктор для преобразования из сущности Game
    public GameDto(com.gamelib.gamelib.model.Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.description = game.getDescription();
        this.releaseDate = game.getReleaseDate();
        this.genre = game.getGenre();
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Set<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(Set<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }
}