package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.mapper.ReviewMapper;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.service.ReviewService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games/{gameId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    // Создание отзыва для игры
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long gameId,
                                                  @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review createdReview = reviewService.createReview(gameId, review);
        return new ResponseEntity<>(reviewMapper.toDto(createdReview), HttpStatus.CREATED);
    }

    // Получение отзыва по ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long gameId, @PathVariable Long id) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(r -> ResponseEntity.ok(reviewMapper.toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Получение всех отзывов для определенной игры
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviewsByGameId(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        List<ReviewDto> reviewDtos = reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
    }

    // Обновление отзыва
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long gameId,
                                                  @PathVariable Long id,
                                                  @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review updatedReview = reviewService.updateReview(gameId, id, review);
        if (updatedReview != null) {
            return new ResponseEntity<>(reviewMapper.toDto(updatedReview), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Удаление отзыва
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long gameId, @PathVariable Long id) {
        boolean deleted = reviewService.deleteReview(gameId, id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                ResponseEntity.notFound().build();
    }
}