package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.mapper.GameMapper;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.service.GameService;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<GameDto>> getGames() {
        List<Game> games = gameService.getAllGames();
        // Ensure collections are loaded within transaction
        games.forEach(game -> {
            if (game.getCompanies() != null) {
                Hibernate.initialize(game.getCompanies()); // Force initialization
            }
            if (game.getReviews() != null) {
                Hibernate.initialize(game.getReviews()); // Force initialization
            }
        });
        return ResponseEntity.ok(gameMapper.toDtoList(games));
    }

    // Добавить новую игру
    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        try {
            Game game = gameMapper.toEntity(gameDto);
            Game createdGame = gameService.createGame(game);
            return new ResponseEntity<>(gameMapper.toDto(createdGame), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if ("Game is already exist".equals(e.getMessage())) {
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
        Game game = gameMapper.toEntity(gameDto);
        Game updatedGame = gameService.updateGame(id, game);
        return updatedGame != null ? ResponseEntity.ok(gameMapper.toDto(updatedGame)) :
                ResponseEntity.notFound().build();
    }

    // Частичное обновление игры (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<GameDto> patchGame(@PathVariable Long id, @RequestBody GameDto gameDto) {
        Game game = gameMapper.toEntity(gameDto);
        Game updatedGame = gameService.patchGame(id, game);
        if (updatedGame != null) {
            return new ResponseEntity<>(gameMapper.toDto(updatedGame), HttpStatus.OK);
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

    // Связь с компаниями
    @PostMapping("/{gameId}/companies/{companyId}")
    public ResponseEntity<GameDto> addCompanyToGame(@PathVariable Long gameId,
                                                    @PathVariable Long companyId) {
        Game updatedGame = gameService.addCompanyToGame(gameId, companyId);
        if (updatedGame != null) {
            return ResponseEntity.ok(gameMapper.toDto(updatedGame));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{gameId}/companies/{companyId}")
    public ResponseEntity<Void> removeCompanyFromGame(@PathVariable Long gameId,
                                                      @PathVariable Long companyId) {
        boolean removed = gameService.removeCompanyFromGame(gameId, companyId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
