package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.mapper.GameMapper;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;
    private final ReviewRepository reviewRepository;

    public GameService(GameRepository gameRepository, CompanyRepository companyRepository, ReviewRepository reviewRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional// Добавьте аннотацию @Transactional
    public List<GameDto> getAllGames() {
        return gameRepository.findAllWithReviews().stream()
                .map(GameMapper::toDto)
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
        if (gameRepository.existsByTitle(gameDto.getTitle())) {
            throw new RuntimeException("Игра с таким названием уже существует."); // Или можете вернуть null, бросить кастомное исключение и т.д.
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
                // Здесь вы можете добавить логику обработки случая, когда компания с таким названием не найдена
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
        Optional<Game> existingGameOptional = gameRepository.findById(id);
        if (existingGameOptional.isPresent()) {
            Game existingGame = existingGameOptional.get();

            // Проверяем, пришло ли новое название в запросе
            if (gameDto.getTitle() != null) {
                // Проверяем, отличается ли новое название от текущего
                if (!existingGame.getTitle().equalsIgnoreCase(gameDto.getTitle())) {
                    // Проверяем, не существует ли уже игра с таким новым названием
                    if (gameRepository.existsByTitle(gameDto.getTitle())) {
                        throw new RuntimeException("Игра с таким названием уже существует.");
                    }
                    existingGame.setTitle(gameDto.getTitle());
                }
            }
            if (gameDto.getReleaseDate() != null) existingGame.setReleaseDate(gameDto.getReleaseDate());
            if (gameDto.getUpdateDate() != null) existingGame.setUpdateDate(gameDto.getUpdateDate());
            if (gameDto.getAvgOnline() != null && gameDto.getAvgOnline() > 0) existingGame.setAvgOnline(gameDto.getAvgOnline());
            if (gameDto.getReviewsSum() != null && gameDto.getReviewsSum() > 0) existingGame.setReviewsSum(gameDto.getReviewsSum());

            if (gameDto.getCompanies() != null) {
                existingGame.getCompanies().clear();
                for (String companyName : gameDto.getCompanies()) {
                    companyRepository.findByName(companyName).ifPresent(company -> {
                        existingGame.getCompanies().add(company);
                        if (company.getGames() == null) {
                            company.setGames(new HashSet<>());
                        }
                        company.getGames().add(existingGame);
                    });
                }
            }
            gameRepository.save(existingGame);
            return gameRepository.findByIdWithCompaniesGraph(id)
                    .map(GameDto::new)
                    .orElse(null);
        }
        return null;
    }

    @Transactional
    public void addReviewToGame(Long gameId, Review review) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            review.setGame(game); // При установке game в review, связь обновится и в game
            reviewRepository.save(review); // Сохраняем отзыв
        } else {
            // Обработка случая, когда игра не найдена
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Игра с ID " + gameId + " не найдена");
        }
    }

    @Transactional
    public void removeReviewFromGame(Long gameId, Long reviewId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (gameOptional.isPresent() && reviewOptional.isPresent()) {
            Game game = gameOptional.get();
            Review reviewToRemove = reviewOptional.get();
            if (game.getReviews().contains(reviewToRemove)) {
                game.removeReview(reviewToRemove); // Используем метод Game для удаления и обновления счетчика
                gameRepository.save(game); // Сохраняем изменения в игре
                reviewRepository.delete(reviewToRemove); // Удаляем сам отзыв
            } else {
                // Обработка случая, когда отзыв не принадлежит этой игре
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отзыв с ID " + reviewId + " не принадлежит игре с ID " + gameId);
            }
        } else {
            // Обработка случая, когда игра или отзыв не найдены
            String message = "";
            if (!gameOptional.isPresent()) {
                message += "Игра с ID " + gameId + " не найдена. ";
            }
            if (!reviewOptional.isPresent()) {
                message += "Отзыв с ID " + reviewId + " не найден.";
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message.trim());
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