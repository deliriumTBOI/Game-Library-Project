package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;

import java.util.stream.Collectors;

public class GameMapper {

    public static GameDto toDto(Game game) {
        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setUpDate(game.getUpDate());
        dto.setAvgOnline(game.getAvgOnline());
        dto.setReviewsSum(game.getReviewsSum());
        dto.setCompanies(game.getCompanies());
        return dto;
    }

    public static Game toEntity(GameDto dto) {
        Game game = new Game();
        return updateEntity(game, dto);
    }

    public static Game updateEntity(Game game, GameDto dto) {
        game.setTitle(dto.getTitle());
        game.setReleaseDate(dto.getReleaseDate());
        game.setUpDate(dto.getUpDate());
        game.setAvgOnline(dto.getAvgOnline());
        game.setReviewsSum(dto.getReviewsSum());
        return game;
    }

    public static Game patchEntity(Game game, GameDto dto) {
        if (dto.getTitle() != null) game.setTitle(dto.getTitle());
        if (dto.getReleaseDate() != null) game.setReleaseDate(dto.getReleaseDate());
        if (dto.getUpDate() != null) game.setUpDate(dto.getUpDate());
        if (dto.getAvgOnline() > 0) game.setAvgOnline(dto.getAvgOnline());
        if (dto.getReviewsSum() > 0) game.setReviewsSum(dto.getReviewsSum());
        return game;
    }
}
