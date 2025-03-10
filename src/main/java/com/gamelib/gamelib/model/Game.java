package com.gamelib.gamelib.model;

public class Game {
    private Long id;
    private String title;
    private Company company;

    public Game(Long id, String title, Company company) {
        this.id = id;
        this.title = title;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
