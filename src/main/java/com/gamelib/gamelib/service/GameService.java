package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {
    Game createGame(Game game);
    Optional<Game> getGameById(Long id);
    List<Game> getAllGames();
    List<Game> getGamesByTitle(String title);
    Game updateGame(Long id, Game updatedGame);
    Game patchGame(Long id, Game partialGame);
    boolean deleteGame(Long id);

    // Методы для связи с компаниями
    Game addCompanyToGame(Long gameId, Long companyId);
    boolean removeCompanyFromGame(Long gameId, Long companyId);
}