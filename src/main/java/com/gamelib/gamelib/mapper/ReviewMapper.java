package com.gamelib.gamelib.mapper;

import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.model.Review;

public class ReviewMapper {
    public static ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setGameId(review.getGame().getId());
        return dto;
    }
}
