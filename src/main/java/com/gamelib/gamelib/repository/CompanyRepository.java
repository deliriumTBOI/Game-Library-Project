package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @EntityGraph(value = "company-with-games", type = EntityGraph.EntityGraphType.LOAD)
    List<Company> findAll(); // Измените имя метода

    @EntityGraph(value = "company-with-games", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Company> findById(Long id); // Измените имя метода

    @EntityGraph(value = "company-with-games", type = EntityGraph.EntityGraphType.LOAD)
    List<Company> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    Optional<Company> findByName(String name); // Добавленная строка
}