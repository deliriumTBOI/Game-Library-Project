package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Company;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private Integer foundedYear;
    private String website;
    private Set<String> gameNames = new HashSet<>(); // Изменено с List<GameDto> на Set<String>

    // Конструктор из модели
    public CompanyDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.description = company.getDescription();
        this.foundedYear = company.getFoundedYear();
        this.website = company.getWebsite();
    }
}