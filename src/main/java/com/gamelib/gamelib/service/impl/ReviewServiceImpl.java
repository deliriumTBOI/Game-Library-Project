package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.repository.ReviewRepository;
import com.gamelib.gamelib.service.ReviewService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, GameRepository gameRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Review createReview(Long gameId, Review review) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Game not found with id: " + gameId));

        review.setGame(game);
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByGameId(Long gameId) {
        // Проверяем существование игры
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with id: " + gameId);
        }
        return reviewRepository.findByGameId(gameId);
    }

    @Override
    @Transactional
    public Review updateReview(Long gameId, Long id, Review updatedReview) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Review not found with id: " + id));

        if (!existingReview.getGame().getId().equals(gameId)) {
            throw new ResourceNotFoundException("Review with id " + id
                    + " does not belong to game with id " + gameId);
        }

        existingReview.setRating(updatedReview.getRating());
        existingReview.setText(updatedReview.getText());
        existingReview.setAuthor(updatedReview.getAuthor());

        return reviewRepository.save(existingReview);
    }

    @Override
    @Transactional
    public boolean deleteReview(Long gameId, Long id) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game not found with id: " + gameId);
        }

        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isEmpty()) {
            return false;
        }

        Review review = reviewOpt.get();
        if (!review.getGame().getId().equals(gameId)) {
            return false;
        }

        reviewRepository.deleteById(id);
        return true;
    }
}