package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    // Получить все игры
    public List<GameDto> getAllGames() {
        return gameRepository.findAll().stream()
                .map(GameDto::new)  // преобразуем Game в GameDto
                .collect(Collectors.toList());
    }

    // Получить игру по ID
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    // Создать новую игру
    public Game createGame(GameDto gameDto) {
        Game game = new Game(gameDto.getTitle(), gameDto.getReleaseDate(), gameDto.getUpDate(), gameDto.getAvgOnline(), gameDto.getReviewsSum(), gameDto.getCompanies());
        return gameRepository.save(game);
    }

    // Обновить игру
    public Game updateGame(Long id, GameDto gameDto) {
        return gameRepository.findById(id)
                .map(game -> {
                    game.setTitle(gameDto.getTitle());
                    game.setReleaseDate(gameDto.getReleaseDate());
                    game.setUpDate(gameDto.getUpDate());
                    game.setAvgOnline(gameDto.getAvgOnline());
                    game.setReviewsSum(gameDto.getReviewsSum());
                    game.setCompanies(gameDto.getCompanies());
                    return gameRepository.save(game);
                })
                .orElse(null);  // если игра не найдена, вернуть null
    }

    // Частичное обновление игры
    public Game patchGame(Long id, GameDto gameDto) {
        return gameRepository.findById(id)
                .map(game -> {
                    if (gameDto.getTitle() != null) game.setTitle(gameDto.getTitle());
                    if (gameDto.getReleaseDate() != null) game.setReleaseDate(gameDto.getReleaseDate());
                    if (gameDto.getUpDate() != null) game.setUpDate(gameDto.getUpDate());
                    if (gameDto.getAvgOnline() >= 0) game.setAvgOnline(gameDto.getAvgOnline());
                    if (gameDto.getReviewsSum() >= 0) game.setReviewsSum(gameDto.getReviewsSum());
                    if (gameDto.getCompanies() != null) game.setCompanies(gameDto.getCompanies());
                    return gameRepository.save(game);
                })
                .orElse(null);  // если игра не найдена, вернуть null
    }

    // Удалить игру
    public boolean deleteGame(Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
