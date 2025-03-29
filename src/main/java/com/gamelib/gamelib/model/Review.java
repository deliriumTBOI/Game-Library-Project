package com.gamelib.gamelib.model;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content; // Отзыв

    @Column(nullable = false)
    private int rating; // Рейтинг (1 - положительный, 0 - нейтральный, -1 - отрицательный)

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game; // Игра, к которой относится отзыв
//
    // Конструкторы
    public Review() {
    }

    public Review(String content, int rating, Game game) {
        this.content = content;
        this.rating = rating;
        this.game = game;
    }

    // Геттеры и сеттеры
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
