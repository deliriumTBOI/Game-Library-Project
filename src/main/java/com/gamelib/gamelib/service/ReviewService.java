package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    public ReviewService(ReviewRepository reviewRepository, GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    // Создание отзыва
    @Transactional
    public Review createReview(Long gameId, Review review) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            review.setGame(game);
            return reviewRepository.save(review);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Game with ID " + gameId + " not found");
        }
    }

    // Чтение отзыва по ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Чтение всех отзывов для определенной игры
    public List<Review> getReviewsByGameId(Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if (gameOptional.isPresent()) {
            return reviewRepository.findByGameId(gameId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Game with ID " + gameId + " not found");
        }
    }

    // Обновление отзыва
    @Transactional
    public Review updateReview(Long id, Review updatedReview) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    if (updatedReview.getContent() != null) {
                        existingReview.setContent(updatedReview.getContent());
                    }
                    if (updatedReview.getRating() != 0) {
                        existingReview.setRating(updatedReview.getRating());
                    }
                    return reviewRepository.save(existingReview);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Review with ID " + id + " not found"));
    }

    // Удаление отзыва
    @Transactional
    public void deleteReview(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            Game game = review.getGame();
            if (game != null) {
                game.removeReview(review); // Обновляем счетчик отзывов в игре
                gameRepository.save(game);
            }
            reviewRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Review with ID " + id + " not found");
        }
    }
}