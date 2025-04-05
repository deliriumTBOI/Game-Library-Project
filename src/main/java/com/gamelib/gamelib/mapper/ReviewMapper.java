package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setText(review.getText());
        dto.setAuthor(review.getAuthor());
        dto.setGameId(review.getGame() != null ? review.getGame().getId() : null);
        return dto;
    }

    public Review toEntity(ReviewDto dto) {
        Review review = new Review();
        review.setId(dto.getId());
        review.setRating(dto.getRating());
        review.setText(dto.getText());
        review.setAuthor(dto.getAuthor());
        // Game устанавливается в сервисе
        return review;
    }
}