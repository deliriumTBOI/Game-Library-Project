package com.gamelib.gamelib.repository;

import com.gamelib.gamelib.model.Company;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    List<Company> findByNameContainingIgnoreCase(String name);

    List<Company> findByNameIn(Collection<String> names);

    Optional<Company> findByName(String name);
}