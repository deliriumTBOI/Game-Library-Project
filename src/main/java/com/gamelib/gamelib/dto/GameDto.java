package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class GameDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private LocalDate updateDate;
    private int avgOnline;
    private int reviewsSum;
    // Убираем поле с полными объектами Company
    // private Set<Company> companies;
    private List<Long> companyIds; // Добавляем поле для ID компаний

    // Конструкторы
    public GameDto() {
    }

    public GameDto(Long id, String title, LocalDate releaseDate, LocalDate updateDate, int avgOnline, int reviewsSum, List<Long> companyIds) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        this.avgOnline = avgOnline;
        this.reviewsSum = reviewsSum;
        this.companyIds = companyIds;
    }

    public GameDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.releaseDate = game.getReleaseDate();
        this.updateDate = game.getUpdateDate();
        this.avgOnline = game.getAvgOnline();
        this.reviewsSum = game.getReviewsSum();
        // Вместо прямого копирования компаний, можно преобразовать их ID в список
        if (game.getCompanies() != null) {
            this.companyIds = game.getCompanies().stream().map(Company::getId).toList();
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

    // Убираем геттер для полных объектов Company
    // public Set<Company> getCompanies() {
    //     return companies;
    // }

    public List<Long> getCompanyIds() {
        return companyIds;
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

    public void setUpdateDate(LocalDate upDate) {
        this.updateDate = upDate;
    }

    public void setAvgOnline(int avgOnline) {
        this.avgOnline = avgOnline;
    }

    public void setReviewsSum(int reviewsSum) {
        this.reviewsSum = reviewsSum;
    }

    // Убираем сеттер для полных объектов Company
    // public void setCompanies(Set<Company> companies) {
    //     this.companies = companies;
    // }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }
}