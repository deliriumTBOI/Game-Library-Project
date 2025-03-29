package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameRepository extends JpaRepository<Game, Long> {
}
