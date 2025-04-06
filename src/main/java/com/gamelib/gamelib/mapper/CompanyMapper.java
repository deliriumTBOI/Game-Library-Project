package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyMapper() {
    }

    public CompanyDto toDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setFoundedYear(company.getFoundedYear());
        dto.setWebsite(company.getWebsite());

        if (company.getGames() != null && Hibernate.isInitialized(company.getGames())) {
            dto.setGameNames(company.getGames().stream()
                    .map(Game::getTitle)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Company toEntity(CompanyDto dto) {
        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setFoundedYear(dto.getFoundedYear());
        company.setWebsite(dto.getWebsite());

        // Заметьте, что мы не устанавливаем games здесь, так как у нас только названия
        // Если нужно сопоставить игры по названиям, это должно быть сделано отдельно

        return company;
    }

    public List<CompanyDto> toDtoList(List<Company> companies) {
        return companies.stream()
                .map(this::toDto)
                .toList();
    }
}