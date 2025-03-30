package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;

public class GameMapper {

    private GameMapper() {
    }

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
                    .map(Company::getName) // Извлекаем название компании
                    .toList()); // Заменено на toList()
        }
        if (game.getReviews() != null) {
            dto.setReviews(game.getReviews().stream()
                    .map(Review::getContent) // Извлекаем содержимое отзыва
                    .toList()); // Заменено на toList()
        }
        return dto;
    }
}