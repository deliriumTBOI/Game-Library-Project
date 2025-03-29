package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository; // Убедитесь, что у вас есть CompanyRepository
import com.gamelib.gamelib.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    public GameService(GameRepository gameRepository, CompanyRepository companyRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    public List<GameDto> getAllGames() {
        return gameRepository.findAll().stream().map(GameDto::new).toList();
    }

    public java.util.Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Transactional
    public Game createGame(GameDto gameDto) {
        Game game = new Game(
                gameDto.getTitle(),
                gameDto.getReleaseDate(),
                gameDto.getUpdateDate(),
                gameDto.getAvgOnline(),
                gameDto.getReviewsSum(),
                new HashSet<>()
        );

        if (game.getUpdateDate() == null) {
            game.setUpdateDate(LocalDate.now());
        }

        if (gameDto.getCompanyIds() != null && !gameDto.getCompanyIds().isEmpty()) {
            Set<Company> companies = new HashSet<>();
            for (Long companyId : gameDto.getCompanyIds()) {
                companyRepository.findById(companyId).ifPresent(company -> {
                    companies.add(company);
                    // Обновляем другую сторону связи
                    if (company.getGames() == null) {
                        company.setGames(new HashSet<>());
                    }
                    company.getGames().add(game);
                });
            }
            game.setCompanies(companies);
        }

        return gameRepository.save(game);
    }

    @Transactional
    public Game updateGame(Long id, GameDto gameDto) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    existingGame.setTitle(gameDto.getTitle());
                    existingGame.setReleaseDate(gameDto.getReleaseDate());
                    existingGame.setUpdateDate(gameDto.getUpdateDate());
                    existingGame.setAvgOnline(gameDto.getAvgOnline());
                    existingGame.setReviewsSum(gameDto.getReviewsSum());
                    // Обновление связей с компаниями аналогично createGame, если необходимо
                    return gameRepository.save(existingGame);
                })
                .orElse(null);
    }

    @Transactional
    public Game patchGame(Long id, GameDto gameDto) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    if (gameDto.getTitle() != null) existingGame.setTitle(gameDto.getTitle());
                    if (gameDto.getReleaseDate() != null) existingGame.setReleaseDate(gameDto.getReleaseDate());
                    if (gameDto.getUpdateDate() != null) existingGame.setUpdateDate(gameDto.getUpdateDate());
                    if (gameDto.getAvgOnline() > 0) existingGame.setAvgOnline(gameDto.getAvgOnline());
                    if (gameDto.getReviewsSum() > 0) existingGame.setReviewsSum(gameDto.getReviewsSum());
                    // Обновление связей с компаниями аналогично createGame, если необходимо
                    return gameRepository.save(existingGame);
                })
                .orElse(null);
    }

    public boolean deleteGame(Long id) {
        return gameRepository.findById(id)
                .map(game -> {
                    gameRepository.delete(game);
                    return true;
                })
                .orElse(false);
    }
}