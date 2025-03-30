package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.mapper.ReviewMapper;
import com.gamelib.gamelib.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/games/{gameId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
        // Внедрите ReviewMapper
    }

    // Создание отзыва для игры
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long gameId, @RequestBody Review review) {
        Review createdReview = reviewService.createReview(gameId, review);
        return new ResponseEntity<>(ReviewMapper.toDto(createdReview), HttpStatus.CREATED);
    }

    // Получение отзыва по ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) { // Измените тип возвращаемого значения здесь
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Получение всех отзывов для определенной игры
    @GetMapping
    public ResponseEntity<List<Review>> getReviewsByGameId(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    // Обновление отзыва
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Review review = reviewService.updateReview(id, updatedReview);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    // Удаление отзыва
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}