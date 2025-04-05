package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByTitle(String title);

    List<Game> findByTitleContainingIgnoreCase(String title);
}