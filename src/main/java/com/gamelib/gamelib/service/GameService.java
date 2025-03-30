package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.mapper.GameMapper;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    public GameService(GameRepository gameRepository, CompanyRepository companyRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional// Добавьте аннотацию @Transactional
    public List<GameDto> getAllGames() {
        return gameRepository.findAllWithReviews().stream()
                .map(GameMapper::toDto)
                .toList();
    }

    public Optional<GameDto> getGameById(Long id) {
        return gameRepository.findByIdWithCompaniesGraph(id)
                .map(GameDto::new);
    }

    public List<GameDto> getGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(GameDto::new)
                .toList();
    }

    @Transactional
    public Game createGame(GameDto gameDto) {
        if (gameRepository.existsByTitle(gameDto.getTitle())) {
            throw new RuntimeException("Game is already exist");
        }

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

        if (gameDto.getCompanies() != null && !gameDto.getCompanies().isEmpty()) {
            Set<Company> companies = new HashSet<>();
            for (String companyName : gameDto.getCompanies()) {
                Optional<Company> companyOptional = companyRepository.findByName(companyName);
                companyOptional.ifPresent(company -> {
                    companies.add(company);
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

                    existingGame.getCompanies().clear(); // Очищаем существующие связи
                    if (gameDto.getCompanies() != null && !gameDto.getCompanies().isEmpty()) {
                        for (String companyName : gameDto.getCompanies()) {
                            companyRepository.findByName(companyName).ifPresent(company -> {
                                existingGame.getCompanies().add(company);
                                if (company.getGames() == null) {
                                    company.setGames(new HashSet<>());
                                }
                                company.getGames().add(existingGame);
                            });
                            // Consider handling the case where the company name doesn't exist
                        }
                    }
                    return gameRepository.save(existingGame);
                })
                .orElse(null);
    }

    @Transactional
    public GameDto patchGame(Long id, GameDto gameDto) {
        return gameRepository.findById(id)
                .map(existingGame -> {
                    updateGameProperties(existingGame, gameDto);
                    updateGameCompanies(existingGame, gameDto.getCompanies());
                    gameRepository.save(existingGame);
                    return gameRepository.findByIdWithCompaniesGraph(id)
                            .map(GameDto::new)
                            .orElse(null);
                })
                .orElse(null);
    }

    private void updateGameProperties(Game existingGame, GameDto gameDto) {
        updateTitle(existingGame, gameDto.getTitle());
        updateReleaseDate(existingGame, gameDto.getReleaseDate());
        updateUpdateDate(existingGame, gameDto.getUpdateDate());
        updateAvgOnline(existingGame, gameDto.getAvgOnline());
        updateReviewsSum(existingGame, gameDto.getReviewsSum());
    }

    private void updateTitle(Game existingGame, String newTitle) {
        if (newTitle != null && !existingGame.getTitle().equalsIgnoreCase(newTitle)) {
            if (gameRepository.existsByTitle(newTitle)) {
                throw new RuntimeException("Игра с таким названием уже существует.");
            }
            existingGame.setTitle(newTitle);
        }
    }

    private void updateReleaseDate(Game existingGame, java.time.LocalDate newReleaseDate) {
        if (newReleaseDate != null) {
            existingGame.setReleaseDate(newReleaseDate);
        }
    }

    private void updateUpdateDate(Game existingGame, LocalDate newUpdateDate) {
        if (newUpdateDate != null) {
            existingGame.setUpdateDate(newUpdateDate);
        }
    }

    private void updateAvgOnline(Game existingGame, Integer newAvgOnline) {
        if (newAvgOnline != null && newAvgOnline > 0) {
            existingGame.setAvgOnline(newAvgOnline);
        }
    }

    private void updateReviewsSum(Game existingGame, Integer newReviewsSum) {
        if (newReviewsSum != null && newReviewsSum > 0) {
            existingGame.setReviewsSum(newReviewsSum);
        }
    }

    private void updateGameCompanies(Game existingGame, List<String> companyNames) {
        if (companyNames != null) {
            existingGame.getCompanies().clear();
            for (String companyName : companyNames) {
                companyRepository.findByName(companyName).ifPresent(company -> {
                    existingGame.getCompanies().add(company);
                    if (company.getGames() == null) {
                        company.setGames(new HashSet<>());
                    }
                    company.getGames().add(existingGame);
                });
            }
        }
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