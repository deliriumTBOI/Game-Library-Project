package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Game;
import java.util.List;

public interface GameService {
    Game createGame(Game game);

    List<Game> getAllGames();

    List<Game> getGamesByTitle(String title);

    Game updateGame(Long id, Game updatedGame);

    Game patchGame(Long id, Game partialGame);

    boolean deleteGame(Long id);

    Game getGameById(Long id);

    Game addCompanyToGame(Long gameId, Long companyId);

    boolean removeCompanyFromGame(Long gameId, Long companyId);

    List<Game> getGamesByMinimumRating(Integer minRating);

    List<Game> getGamesByRatingRange(Integer minRating, Integer maxRating);

    void clearCache();
}