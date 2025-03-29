package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    public GameService(GameRepository gameRepository, CompanyRepository companyRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    public List<GameDto> getAllGames() {
        return gameRepository.findAllWithCompaniesGraph().stream()
                .map(GameDto::new)
                .collect(Collectors.toList());
    }

    public Optional<GameDto> getGameById(Long id) {
        return gameRepository.findByIdWithCompaniesGraph(id)
                .map(GameDto::new);
    }

    public List<GameDto> getGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(GameDto::new)
                .collect(Collectors.toList());
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

        if (gameDto.getCompanies() != null && !gameDto.getCompanies().isEmpty()) {
            Set<Company> companies = new HashSet<>();
            for (Map<String, Long> companyMap : gameDto.getCompanies()) {
                Long companyId = companyMap.get("id");
                if (companyId != null) {
                    companyRepository.findById(companyId).ifPresent(company -> {
                        companies.add(company);
                        if (company.getGames() == null) {
                            company.setGames(new HashSet<>());
                        }
                        company.getGames().add(game);
                    });
                }
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
                        for (Map<String, Long> companyMap : gameDto.getCompanies()) {
                            Long companyId = companyMap.get("id");
                            if (companyId != null) {
                                companyRepository.findById(companyId).ifPresent(company -> {
                                    existingGame.getCompanies().add(company);
                                    if (company.getGames() == null) {
                                        company.setGames(new HashSet<>());
                                    }
                                    company.getGames().add(existingGame);
                                });
                            }
                        }
                    }
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

                    if (gameDto.getCompanies() != null) {
                        existingGame.getCompanies().clear(); // Очищаем существующие связи
                        for (Map<String, Long> companyMap : gameDto.getCompanies()) {
                            Long companyId = companyMap.get("id");
                            if (companyId != null) {
                                companyRepository.findById(companyId).ifPresent(company -> {
                                    existingGame.getCompanies().add(company);
                                    if (company.getGames() == null) {
                                        company.setGames(new HashSet<>());
                                    }
                                    company.getGames().add(existingGame);
                                });
                            }
                        }
                    }
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