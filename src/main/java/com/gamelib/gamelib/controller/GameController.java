package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.GameDto;
import com.gamelib.gamelib.exception.InvalidInputException;
import com.gamelib.gamelib.mapper.GameMapper;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/games")
@Tag(name = "Games", description = "API для управления играми")
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;

    public GameController(GameService gameService, GameMapper gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Получить список игр", description = "Возвращает список всех игр или игр, "
            + "соответствующих указанному названию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список игр успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "400", description = "Название игры не найдено")
    })
    public ResponseEntity<List<GameDto>> getGames(
            @Parameter(description = "Название игры (опционально)")
            @RequestParam(required = false) String title) {

        List<Game> games;

        if (title != null && !title.isEmpty()) {
            games = gameService.getGamesByTitle(title);
            if (games.isEmpty()) {
                throw new InvalidInputException("Title not found");
            }
        } else {
            games = gameService.getAllGames();
        }

        games.forEach(game -> {
            if (game.getCompanies() != null) {
                Hibernate.initialize(game.getCompanies());
            }
            if (game.getReviews() != null) {
                Hibernate.initialize(game.getReviews());
            }
        });

        return ResponseEntity.ok(gameMapper.toDtoList(games));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Получить игру по ID", description = "Возвращает игру по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Игра успешно найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "404", description = "Игра не найдена",
                    content = @Content)
    })
    public ResponseEntity<GameDto> getGameById(
            @Parameter(description = "ID игры", required = true) @PathVariable Long id) {
        Game game = gameService.getGameById(id);
        if (game != null) {

            if (game.getCompanies() != null) {
                Hibernate.initialize(game.getCompanies());
            }
            if (game.getReviews() != null) {
                Hibernate.initialize(game.getReviews());
            }
            return ResponseEntity.ok(gameMapper.toDto(game));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Создать новую игру", description = "Создает новую игру "
            + "с указанными данными")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Игра успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные для создания игры",
                    content = @Content),
        @ApiResponse(responseCode = "409", description = "Игра с таким названием уже существует",
                    content = @Content)
    })
    public ResponseEntity<GameDto> createGame(
            @Parameter(description = "Данные новой игры", required = true)
            @Valid @RequestBody GameDto gameDto) {
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

    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько игр", description = "Создает список новых игр")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Игры успешно созданы",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные для создания игр",
                    content = @Content)
    })
    public ResponseEntity<List<GameDto>> createGames(
            @Valid @RequestBody List<@Valid GameDto> gameDtos) {
        List<Game> games = gameDtos.stream()
                .map(gameMapper::toEntity)
                .collect(Collectors.toList());

        List<Game> createdGames = gameService.createGames(games);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameMapper.toDtoList(createdGames));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить игру", description = "Полное обновление игры по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Игра успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "404", description = "Игра не найдена",
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления игры",
                    content = @Content)
    })
    public ResponseEntity<GameDto> updateGame(
            @Parameter(description = "ID игры", required = true) @PathVariable Long id,
            @Parameter(description = "Обновленные данные игры", required = true)
            @Valid @RequestBody GameDto gameDto) {
        Game game = gameMapper.toEntity(gameDto);
        Game updatedGame = gameService.updateGame(id, game);
        return updatedGame != null ? ResponseEntity.ok(gameMapper.toDto(updatedGame)) :
                ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частично обновить игру", description = "Частичное обновление "
            + "игры по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Игра успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "404", description = "Игра не найдена",
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления игры",
                    content = @Content)
    })
    public ResponseEntity<GameDto> patchGame(
            @Parameter(description = "ID игры", required = true) @PathVariable Long id,
            @Parameter(description = "Частичные данные для обновления", required = true)
            @RequestBody GameDto gameDto) {
        Game game = gameMapper.toEntity(gameDto);
        Game updatedGame = gameService.patchGame(id, game);
        if (updatedGame != null) {
            return new ResponseEntity<>(gameMapper.toDto(updatedGame), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить игру", description = "Удаляет игру по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Игра успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Игра не найдена")
    })
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "ID игры для удаления", required = true)
            @PathVariable Long id) {
        boolean isDeleted = gameService.deleteGame(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{gameId}/companies/{companyId}")
    @Operation(summary = "Добавить компанию к игре",
            description = "Добавляет связь между игрой и компанией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Компания успешно добавлена к игре",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDto.class))),
        @ApiResponse(responseCode = "404", description = "Игра или компания не найдена")
    })
    public ResponseEntity<GameDto> addCompanyToGame(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID компании", required = true) @PathVariable Long companyId) {
        Game updatedGame = gameService.addCompanyToGame(gameId, companyId);
        if (updatedGame != null) {
            return ResponseEntity.ok(gameMapper.toDto(updatedGame));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-rating")
    @Transactional(readOnly = true)
    @Operation(summary = "Получить игры по рейтингу",
            description = "Возвращает игры с указанным минимальным/максимальным рейтингом")
    public ResponseEntity<List<GameDto>> getGamesByRating(
            @Parameter(description = "Минимальный рейтинг")
            @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Максимальный рейтинг")
            @RequestParam(required = false) Integer maxRating) {

        validateRatingRange(minRating, maxRating);

        List<Game> games = fetchGamesByRating(minRating, maxRating);

        initializeGameRelations(games);

        return ResponseEntity.ok(gameMapper.toDtoList(games));
    }

    private void validateRatingRange(Integer minRating, Integer maxRating) {
        if (minRating != null && (minRating < 0 || minRating > 10)) {
            throw new InvalidInputException("Minimum rating must be between 0 and 10");
        }

        if (maxRating != null && (maxRating < 0 || maxRating > 10)) {
            throw new InvalidInputException("Maximum rating must be between 0 and 10");
        }

        if (minRating != null && maxRating != null && minRating > maxRating) {
            throw new InvalidInputException("Minimum rating cannot be greater than maximum rating");
        }
    }

    private List<Game> fetchGamesByRating(Integer minRating, Integer maxRating) {
        if (minRating != null && maxRating != null) {
            return gameService.getGamesByRatingRange(minRating, maxRating);
        }

        if (minRating != null) {
            return gameService.getGamesByMinimumRating(minRating);
        }

        return gameService.getAllGames();
    }

    private void initializeGameRelations(List<Game> games) {
        games.forEach(game -> {
            if (game.getCompanies() != null) {
                Hibernate.initialize(game.getCompanies());
            }
            if (game.getReviews() != null) {
                Hibernate.initialize(game.getReviews());
            }
        });
    }

    @DeleteMapping("/{gameId}/companies/{companyId}")
    @Operation(summary = "Удалить компанию из игры",
            description = "Удаляет связь между игрой и компанией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Связь успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Игра или компания не найдена")
    })
    public ResponseEntity<Void> removeCompanyFromGame(
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId,
            @Parameter(description = "ID компании", required = true) @PathVariable Long companyId) {
        boolean removed = gameService.removeCompanyFromGame(gameId, companyId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}