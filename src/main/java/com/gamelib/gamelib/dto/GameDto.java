package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Game;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private Long id;
    private String title;
    private String description;
    private Date releaseDate;
    private String genre;
    private Set<String> companyNames = new HashSet<>(); // Изменено с companyIds на companyNames
    private List<ReviewDto> reviews = new ArrayList<>();

    // Конструктор из модели
    public GameDto(Game game) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.description = game.getDescription();
        this.releaseDate = game.getReleaseDate();
        this.genre = game.getGenre();
    }
}