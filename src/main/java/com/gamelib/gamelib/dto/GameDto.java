package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Game;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private LocalDate updateDate;
    private int avgOnline;
    private int reviewsSum;
    private List<Map<String, Long>> companies;

    // Конструкторы
    public GameDto() {
    }

    public GameDto(Long id, String title, LocalDate releaseDate, LocalDate updateDate, int avgOnline, int reviewsSum, List<Map<String, Long>> companies) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        this.avgOnline = avgOnline;
        this.reviewsSum = reviewsSum;
        this.companies = companies;
    }

    public GameDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.releaseDate = game.getReleaseDate();
        this.updateDate = game.getUpdateDate();
        this.avgOnline = game.getAvgOnline();
        this.reviewsSum = game.getReviewsSum();
        if (game.getCompanies() != null) {
            this.companies = game.getCompanies().stream()
                    .map(company -> Map.of("id", company.getId()))
                    .collect(Collectors.toList());
        }
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

    public int getAvgOnline() {
        return avgOnline;
    }

    public int getReviewsSum() {
        return reviewsSum;
    }

    public List<Map<String, Long>> getCompanies() {
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

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public void setAvgOnline(int avgOnline) {
        this.avgOnline = avgOnline;
    }

    public void setReviewsSum(int reviewsSum) {
        this.reviewsSum = reviewsSum;
    }

    public void setCompanies(List<Map<String, Long>> companies) {
        this.companies = companies;
    }
}