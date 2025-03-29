package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Получить все отзывы
    @GetMapping
    public List<ReviewDto> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // Получить отзыв по ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(new ReviewDto(review)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Добавить новый отзыв
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        Review createdReview = reviewService.createReview(reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewDto(createdReview));
    }
//
    // Обновить отзыв
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewDto reviewDto) {
        Review updatedReview = reviewService.updateReview(id, reviewDto);
        return updatedReview != null ? ResponseEntity.ok(new ReviewDto(updatedReview)) :
                ResponseEntity.notFound().build();
    }

    // Удалить отзыв
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        boolean isDeleted = reviewService.deleteReview(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
