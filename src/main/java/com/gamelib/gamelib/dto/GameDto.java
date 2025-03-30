package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GameDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private LocalDate updateDate;
    private Integer avgOnline;
    private Integer reviewsSum;
    private List<String> companies;

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
                .collect(Collectors.toList());
    }

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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getAvgOnline() {
        return avgOnline;
    }

    public void setAvgOnline(Integer avgOnline) {
        this.avgOnline = avgOnline;
    }

    public Integer getReviewsSum() {
        return reviewsSum;
    }

    public void setReviewsSum(Integer reviewsSum) {
        this.reviewsSum = reviewsSum;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }
}