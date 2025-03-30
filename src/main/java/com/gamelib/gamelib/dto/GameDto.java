package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;
import java.time.LocalDate;
import java.util.List;

public class GameDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private LocalDate updateDate;
    private Integer avgOnline; // Изменили тип на Integer
    private Integer reviewsSum; // Изменили тип на Integer
    private List<String> companies;
    private List<String> reviews;

    public GameDto() {
    }

    public GameDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.releaseDate = game.getReleaseDate();
        this.updateDate = game.getUpdateDate();
        this.avgOnline = game.getAvgOnline();
        this.reviewsSum = game.getReviewsSum();
        this.companies = game.getCompanies().stream()
                .map(Company::getName)
                .toList();
        this.reviews = game.getReviews().stream()
                .map(Review::getContent)
                .toList();
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public Integer getAvgOnline() { // Изменили тип возвращаемого значения
        return avgOnline;
    }

    public Integer getReviewsSum() { // Изменили тип возвращаемого значения
        return reviewsSum;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public List<String> getReviews() {
        return reviews;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public void setAvgOnline(Integer avgOnline) { // Изменили тип параметра
        this.avgOnline = avgOnline;
    }

    public void setReviewsSum(Integer reviewsSum) { // Изменили тип параметра
        this.reviewsSum = reviewsSum;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}