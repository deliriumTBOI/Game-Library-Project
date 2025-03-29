package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
