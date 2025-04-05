package com.gamelib.gamelib.dto;

public class ReviewDto {
    private Long id;
    private Integer rating;
    private String text;
    private String author;
    private Long gameId;

    public ReviewDto() {
    }

    public ReviewDto(Long id, Integer rating, String text, String author, Long gameId) {
        this.id = id;
        this.rating = rating;
        this.text = text;
        this.author = author;
        this.gameId = gameId;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}