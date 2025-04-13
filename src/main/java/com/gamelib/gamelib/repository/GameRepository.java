package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByTitle(String title);

    List<Game> findByTitleIgnoreCase(String title);

    // JPQL запрос для получения игр по минимальному рейтингу отзывов
    @Query("SELECT DISTINCT g FROM Game g JOIN g.reviews r WHERE r.rating >= :minRating")
    List<Game> findGamesByMinimumRating(@Param("minRating") Integer minRating);

    // Native SQL запрос для получения игр по диапазону рейтинга
    @Query(value = "SELECT DISTINCT g.* FROM games g JOIN reviews r ON g.id = r.game_id "
            + "WHERE r.rating BETWEEN :minRating AND :maxRating", nativeQuery = true)
    List<Game> findGamesByRatingRange(@Param("minRating") Integer minRating,
                                      @Param("maxRating") Integer maxRating);
}