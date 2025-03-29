package com.gamelib.gamelib.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "game")
@NamedEntityGraph(name = "game-with-companies",
        attributeNodes = @NamedAttributeNode("companies"))
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//
    @NotBlank(message = "Title cannot be blank") // Проверка на пустое значение
    @Column(nullable = false) // Указание, что столбец не может быть null в базе данных
    private String title;

    @NotNull(message = "Release date cannot be null") // Проверка на null
    @Column(name = "release_date", nullable = false) // Указание на столбец с именем "release_date"
    private LocalDate releaseDate;

    @NotNull(message = "Update date cannot be null") // Проверка на null
    @Column(name = "update_date", nullable = false) // Указание на столбец с именем "update_date"
    private LocalDate updateDate;

    @NotNull(message = "Average online cannot be null") // Проверка на null
    @Column(name = "average_online", nullable = false) // Указание на столбец с именем "average_online"
    private int avgOnline;

    @Column(name = "reviews_amount", nullable = false) // Указание на столбец с именем "reviews_amount"
    private int reviewsSum;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_company",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    //@JsonManagedReference
    private Set<Company> companies;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews;

    // Конструкторы

    public Game(String title, LocalDate releaseDate, LocalDate updateDate, int avgOnline, int reviewsSum, Set<Company> companies) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        this.avgOnline = avgOnline;
        this.reviewsSum = reviewsSum;
        this.companies = companies;
    }

    public Game() {
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

    public Set<Company> getCompanies() {
        return companies;
    }

    public Set<Review> getReviews() {
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

    public void setAvgOnline(int avgOnline) {
        this.avgOnline = avgOnline;
    }

    public void setReviewsSum(int reviewsSum) {
        this.reviewsSum = reviewsSum;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }
}
