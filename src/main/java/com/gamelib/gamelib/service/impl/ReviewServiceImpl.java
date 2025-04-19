package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.cache.LruCache;
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
    private static final String CACHE_REVIEW_PREFIX = "review:id:";

    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final LruCache<String, Review> reviewCache = new LruCache<>(50000, 100, "ReviewCache");

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
    @Transactional
    public List<Review> createReviews(Long gameId, List<Review> reviews) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: "
                        + gameId));

        for (Review review : reviews) {
            review.setGame(game);
        }

        return reviewRepository.saveAll(reviews);
    }


    @Override
    public Optional<Review> getReviewById(Long id) {
        String cacheKey = CACHE_REVIEW_PREFIX + id;

        // Проверяем наличие в кэше
        if (reviewCache.containsKey(cacheKey)) {
            Review cachedReview = reviewCache.get(cacheKey);
            return Optional.ofNullable(cachedReview);
        }

        // Если нет в кэше, получаем из репозитория
        Optional<Review> reviewOpt = reviewRepository.findById(id);

        // Если обзор найден, сохраняем в кэш
        reviewOpt.ifPresent(review -> reviewCache.put(cacheKey, review));

        return reviewOpt;
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

        Review result = reviewRepository.save(existingReview);

        // Обновляем кэш
        String cacheKey = CACHE_REVIEW_PREFIX + id;
        reviewCache.put(cacheKey, result);

        return result;
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

        // Удаляем из кэша
        String cacheKey = CACHE_REVIEW_PREFIX + id;
        reviewCache.remove(cacheKey);

        return true;
    }
}