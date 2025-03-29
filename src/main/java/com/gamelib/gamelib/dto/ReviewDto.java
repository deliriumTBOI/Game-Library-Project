package com.gamelib.gamelib.dto;

import com.gamelib.gamelib.model.Review;

public class ReviewDto {
    private Long id;
    private String content;
    private int rating;
    private Long gameId; // ID игры, к которой относится отзыв

    public ReviewDto() {
    }
    
    public ReviewDto(Long id, String content, int rating, Long gameId) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.gameId = gameId;
    }

    public ReviewDto(Review review) {
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public Long getGameId() {
        return gameId;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
