package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(Long gameId, Review review);

    List<Review> createReviews(Long gameId, List<Review> reviews);

    Optional<Review> getReviewById(Long id);

    List<Review> getReviewsByGameId(Long gameId);

    Review updateReview(Long gameId, Long id, Review updatedReview);

    boolean deleteReview(Long gameId, Long id);
}