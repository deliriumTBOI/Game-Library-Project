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
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = service.getAllGames();
        return games.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(games);
    }

    @GetMapping("/search")
    public ResponseEntity<Game> getGameByQuery(@RequestParam String title) {
        return getGameResponse(title);
    }

    @GetMapping("/{title}")
    public ResponseEntity<Game> getGameByPath(@PathVariable String title) {
        return getGameResponse(title);
    }

    private ResponseEntity<Game> getGameResponse(String title) {
        Game game = service.getGameByTitle(title);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    @GetMapping("/company")
    public ResponseEntity<List<Game>> getGamesByCompany(@RequestParam String name) {
        List<Game> games = service.getGamesByCompanyName(name);
        return games.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(games);
    }
}
