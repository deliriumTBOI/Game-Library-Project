package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.GameRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public List<Game> getAllGames() {
        return repository.findAll();
    }

    public Game getGameByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<Game> getGamesByCompanyName(String companyName) {
        return repository.findByCompanyName(companyName);
    }
}
