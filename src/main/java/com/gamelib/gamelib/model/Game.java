package com.gamelib.gamelib.model;

import com.gamelib.gamelib.dto.CompanyDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"companies", "reviews"})
@EqualsAndHashCode(of = {"id"})
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

    @ManyToMany(cascade
            = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "game_company",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    private Set<Company> companies = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Game(String title, String description, Date releaseDate, String genre) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    // Метод для получения имен компаний
    public Set<String> getCompanyNames() {
        return this.companies.stream()
                .map(Company::getName)
                .collect(Collectors.toSet());
    }

    // Метод для получения компаний в виде DTO
    public Set<CompanyDto> getCompanyDtos() {
        return this.companies.stream()
                .map(company -> new CompanyDto(
                        company.getId(),
                        company.getName(),
                        company.getDescription(),
                        company.getFoundedYear(),
                        company.getWebsite(),
                        company.getGameTitles() // используем метод из Company
                ))
                .collect(Collectors.toSet());
    }
}