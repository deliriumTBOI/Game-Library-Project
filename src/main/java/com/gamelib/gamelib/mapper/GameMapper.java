package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository; // Импортируйте CompanyRepository
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameMapper {

    public static GameDto toDto(Game game) {
        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setUpdateDate(game.getUpdateDate());
        dto.setAvgOnline(game.getAvgOnline());
        dto.setReviewsSum(game.getReviewsSum());
        if (game.getCompanies() != null) {
            dto.setCompanies(game.getCompanies().stream()
                    .map(company -> Map.of("id", company.getId()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static Game toEntity(GameDto dto, CompanyRepository companyRepository) {
        Game game = new Game();
        return updateEntity(game, dto, companyRepository);
    }

    public static Game updateEntity(Game game, GameDto dto, CompanyRepository companyRepository) {
        game.setTitle(dto.getTitle());
        game.setReleaseDate(dto.getReleaseDate());
        game.setUpdateDate(dto.getUpdateDate());
        game.setAvgOnline(dto.getAvgOnline());
        game.setReviewsSum(dto.getReviewsSum());
        if (dto.getCompanies() != null && !dto.getCompanies().isEmpty()) {
            Set<Company> companies = new HashSet<>();
            for (Map<String, Long> companyMap : dto.getCompanies()) {
                Long companyId = companyMap.get("id");
                if (companyId != null) {
                    companyRepository.findById(companyId).ifPresent(companies::add);
                }
            }
            game.setCompanies(companies);
        }
        return game;
    }

    public static Game patchEntity(Game game, GameDto dto, CompanyRepository companyRepository) {
        if (dto.getTitle() != null) game.setTitle(dto.getTitle());
        if (dto.getReleaseDate() != null) game.setReleaseDate(dto.getReleaseDate());
        if (dto.getUpdateDate() != null) game.setUpdateDate(dto.getUpdateDate());
        if (dto.getAvgOnline() > 0) game.setAvgOnline(dto.getAvgOnline());
        if (dto.getReviewsSum() > 0) game.setReviewsSum(dto.getReviewsSum());
        if (dto.getCompanies() != null) {
            Set<Company> companies = new HashSet<>();
            for (Map<String, Long> companyMap : dto.getCompanies()) {
                Long companyId = companyMap.get("id");
                if (companyId != null) {
                    companyRepository.findById(companyId).ifPresent(companies::add);
                }
            }
            game.setCompanies(companies);
        }
        return game;
    }
}