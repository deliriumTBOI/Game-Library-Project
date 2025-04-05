package com.gamelib.gamelib.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(length = 2000)
    private String description;

    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    private String genre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "game_company",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private Set<Company> companies = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // Конструкторы
    public Game() {
    }

    public Game(String title, String description, Date releaseDate, String genre) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.genre = genre;
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

    public Set<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Вспомогательные методы для управления связями
    public void addCompany(Company company) {
        this.companies.add(company);
        company.getGames().add(this);
    }

    public void removeCompany(Company company) {
        this.companies.remove(company);
        company.getGames().remove(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setGame(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setGame(null);
    }
}