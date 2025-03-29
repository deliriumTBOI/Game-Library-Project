package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.time.LocalDate;
import java.util.Set;

public class GameDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private LocalDate upDate;
    private int avgOnline;
    private int reviewsSum;
    private Set<Company> companies; // Названия компаний

    // Конструкторы
    public GameDto() {
    }

    public GameDto(Long id, String title, LocalDate releaseDate, LocalDate upDate, int avgOnline, int reviewsSum, Set<Company> companies) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.upDate = upDate;
        this.avgOnline = avgOnline;
        this.reviewsSum = reviewsSum;
        this.companies = companies;
    }

    public GameDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.releaseDate = game.getReleaseDate();
        this.upDate = game.getUpDate();
        this.avgOnline = game.getAvgOnline();
        this.reviewsSum = game.getReviewsSum();
        this.companies = game.getCompanies();
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

    public LocalDate getUpDate() {
        return upDate;
    }

    public int getAvgOnline() {
        return avgOnline;
    }

    public int getReviewsSum() {
        return reviewsSum;
    }

    public Set<Company> getCompanies() {
        return companies;
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

    public void setUpDate(LocalDate upDate) {
        this.upDate = upDate;
    }

    public void setAvgOnline(int avgOnline) {
        this.avgOnline = avgOnline;
    }

    public void setReviewsSum(int reviewsSum) {
        this.reviewsSum = reviewsSum;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }
}
