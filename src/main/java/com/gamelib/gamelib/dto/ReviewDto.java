package com.gamelib.gamelib.dto;

public class ReviewDto {
    private Long id;
    private String content;
    private int rating;
    private Long gameId;

    public ReviewDto() {
    }

    public ReviewDto(Long id, String content, int rating, Long gameId) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.gameId = gameId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}