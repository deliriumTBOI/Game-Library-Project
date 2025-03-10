package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {
    private final List<Game> games = new ArrayList<>();

    public GameRepository() {
        games.add(new Game("The Witcher 3", new Company("CD Projekt Red", "Poland")));
        games.add(new Game("Cyberpunk 2077", new Company("CD Projekt Red", "Poland")));
        games.add(new Game("God of War", new Company("Santa Monica Studio", "USA")));
        //games.add(new Game("The Binding Of Isaac", new Company("Nicalis", "USA")));
    }

    public List<Game> findAll() {
        return games;
    }

    public Game findByTitle(String title) {
        return games.stream().filter(g ->
                g.getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
    }

    public List<Game> findByCompanyName(String companyName) {
        return games.stream()
                .filter(g -> g.getCompany().getName().equalsIgnoreCase(companyName))
                .toList();
    }
}

