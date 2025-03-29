package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.util.stream.Collectors;
import java.util.Collections;
//
public class CompanyMapper {
    public static CompanyDto toDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setCountry(company.getCountry());
        dto.setGames(company.getGames() != null
                ? company.getGames().stream().map(Game::getTitle).collect(Collectors.toSet())
                : Collections.emptySet());

        return dto;
    }
}
