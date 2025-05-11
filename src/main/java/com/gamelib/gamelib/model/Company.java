package com.gamelib.gamelib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gamelib.gamelib.dto.CompanyDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"games"})
@EqualsAndHashCode(of = {"id"})
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 2000)
    private String description;

    private Integer foundedYear;

    private String website;

    @ManyToMany(mappedBy = "companies", cascade
            = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnore
    private Set<Game> games = new HashSet<>();

    // Метод для получения названий игр компании
    public Set<String> getGameTitles() {
        return this.games.stream()
                .map(Game::getTitle)
                .collect(Collectors.toSet());
    }

    // Метод для преобразования в DTO
    public CompanyDto toDto() {
        return new CompanyDto(
                this.id,
                this.name,
                this.description,
                this.foundedYear,
                this.website,
                this.getGameTitles()
        );
    }
}