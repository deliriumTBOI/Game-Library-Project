package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // Получить все отзывы
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewDto::new)  // преобразуем Review в ReviewDto
                .collect(Collectors.toList());
    }

    // Получить отзыв по ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Создать новый отзыв
    public Review createReview(ReviewDto reviewDto) {
        // Поскольку ReviewDto содержит только ID игры, нам нужно получить саму игру
        Review review = new Review(reviewDto.getContent(), reviewDto.getRating(), null); // Значение Game установится позже
        return reviewRepository.save(review);
    }

    // Обновить отзыв
    public Review updateReview(Long id, ReviewDto reviewDto) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setContent(reviewDto.getContent());
                    review.setRating(reviewDto.getRating());
                    return reviewRepository.save(review);
                })
                .orElse(null);  // если отзыв не найден, вернуть null
    }

    // Удалить отзыв
    public boolean deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
