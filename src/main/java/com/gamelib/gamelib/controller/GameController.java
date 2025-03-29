package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Получить все игры
    @GetMapping
    public List<GameDto> getAllGames() {
        return gameService.getAllGames();
    }

    // Получить игру по ID
    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity.ok(new GameDto(game)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Добавить новую игру
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        Game createdGame = gameService.createGame(gameDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GameDto(createdGame));
    }

    // Обновить игру
    @PutMapping("/{id}")
    public ResponseEntity<GameDto> updateGame(@PathVariable Long id, @RequestBody GameDto gameDto) {
        Game updatedGame = gameService.updateGame(id, gameDto);
        return updatedGame != null ? ResponseEntity.ok(new GameDto(updatedGame)) :
                ResponseEntity.notFound().build();
    }

    // Частичное обновление игры (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<GameDto> patchGame(@PathVariable Long id, @RequestBody GameDto gameDto) {
        Game updatedGame = gameService.patchGame(id, gameDto);
        return updatedGame != null ? ResponseEntity.ok(new GameDto(updatedGame)) :
                ResponseEntity.notFound().build();
    }

    // Удалить игру
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        boolean isDeleted = gameService.deleteGame(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
