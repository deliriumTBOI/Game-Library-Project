package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.service.GameService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping
    public List<Game> getAllGames() {
        return service.getAllGames();
    }

    @GetMapping("/search")
    public ResponseEntity<Game> getGameByQuery(@RequestParam String title) {
        Game game = service.getGameByTitle(title);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = service.getGameById(id);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    @GetMapping("/company")
    public List<Game> getGamesByCompany(@RequestParam String name) {
        return service.getGamesByCompanyName(name);
    }
}
