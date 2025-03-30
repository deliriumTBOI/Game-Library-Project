package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.service.GameService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Получить игру по ID
    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames(@RequestParam(
            value = "title", required = false) String title) {
        if (title != null) {
            List<GameDto> games = gameService.getGamesByTitle(title);
            return ResponseEntity.ok(games);
        } else {
            List<GameDto> games = gameService.getAllGames();
            return ResponseEntity.ok(games);
        }
    }

    // Добавить новую игру
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        try {
            Game createdGame = gameService.createGame(gameDto);
            return new ResponseEntity<>(new GameDto(createdGame), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Game is already exist")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Server Error: " + e.getMessage());
            }
        }
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
        GameDto updatedGameDto = gameService.patchGame(id, gameDto);
        if (updatedGameDto != null) {
            return new ResponseEntity<>(updatedGameDto, HttpStatus.OK); // Возвращаем GameDto
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Удалить игру
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        boolean isDeleted = gameService.deleteGame(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
