package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.mapper.ReviewMapper;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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
@Tag(name = "Reviews", description = "API для управления отзывами на игры")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping
    @Operation(summary = "Создать новый отзыв", description = "Создает новый отзыв"
            + " для указанной игры")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Отзыв успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные для создания отзыва",
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Игра не найдена",
                    content = @Content)
    })
    public ResponseEntity<ReviewDto> createReview(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId,
            @Parameter(description = "Данные нового отзыва", required = true)
            @Valid @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review createdReview = reviewService.createReview(gameId, review);
        return new ResponseEntity<>(reviewMapper.toDto(createdReview), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отзыв по ID", description = "Возвращает отзыв по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отзыв успешно найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "404", description = "Отзыв не найден",
                    content = @Content)
    })
    public ResponseEntity<ReviewDto> getReviewById(
            @Parameter(description = "ID отзыва", required = true) @PathVariable Long id,
            @Parameter(description = "ID игры", required = true) @PathVariable String gameId) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(r -> ResponseEntity.ok(reviewMapper.toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Получить все отзывы для игры", description = "Возвращает список "
            + "всех отзывов для указанной игры")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список отзывов успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "404", description = "Игра не найдена",
                    content = @Content)
    })
    public ResponseEntity<List<ReviewDto>> getReviewsByGameId(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        List<ReviewDto> reviewDtos = reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
        return new ResponseEntity<>(reviewDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить отзыв", description = "Обновляет отзыв по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отзыв успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDto.class))),
        @ApiResponse(responseCode = "404", description = "Отзыв или игра не найдены",
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Некорректные данные "
                + "для обновления отзыва",
                    content = @Content)
    })
    public ResponseEntity<ReviewDto> updateReview(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID отзыва", required = true) @PathVariable Long id,
            @Parameter(description = "Обновленные данные отзыва", required = true)
            @Valid @RequestBody ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review updatedReview = reviewService.updateReview(gameId, id, review);
        if (updatedReview != null) {
            return new ResponseEntity<>(reviewMapper.toDto(updatedReview), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отзыв", description = "Удаляет отзыв по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Отзыв успешно удален"),
        @ApiResponse(responseCode = "404", description = "Отзыв или игра не найдены",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID отзыва для удаления", required = true)
            @PathVariable Long id) {
        boolean deleted = reviewService.deleteReview(gameId, id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                ResponseEntity.notFound().build();
    }
}