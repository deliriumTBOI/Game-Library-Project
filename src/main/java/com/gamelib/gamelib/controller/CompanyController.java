package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.mapper.CompanyMapper;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.service.CompanyService;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    public CompanyController(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<CompanyDto>>
        getAllCompanies(@RequestParam(value = "name", required = false) String name) {
        List<Company> companies;
        if (name != null) {
            companies = companyService.getCompaniesByName(name);
        } else {
            companies = companyService.getAllCompanies();
        }

        companies.forEach(company -> {
            if (company.getGames() != null) {
                Hibernate.initialize(company.getGames());
            }
        });

        return ResponseEntity.ok(companyMapper.toDtoList(companies));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(company -> {
                    if (company.getGames() != null) {
                        Hibernate.initialize(company.getGames());
                    }
                    return ResponseEntity.ok(companyMapper.toDto(company));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createCompany(@RequestBody CompanyDto companyDto) {
        try {
            Company company = companyMapper.toEntity(companyDto);
            Company createdCompany = companyService.createCompany(company);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(companyMapper.toDto(createdCompany));
        } catch (RuntimeException e) {
            if ("Company is already exist".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Company is already exist");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Server Error: " + e.getMessage());
            }
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id,
                                                    @RequestBody CompanyDto companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        Company updatedCompany = companyService.updateCompany(id, company);

        if (updatedCompany != null && updatedCompany.getGames() != null) {
            Hibernate.initialize(updatedCompany.getGames());
        }

        return updatedCompany != null ? ResponseEntity.ok(companyMapper.toDto(updatedCompany)) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCompany(@PathVariable Long id) {
        boolean isDeleted = companyService.deleteCompany(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company with ID " + id + " not found");
        }
    }

    @GetMapping("/{companyId}/games")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CompanyDto>> getGamesForCompany(@PathVariable Long companyId) {
        return companyService.getCompanyById(companyId)
                .map(company -> {
                    if (company.getGames() != null) {
                        Hibernate.initialize(company.getGames());
                    }
                    return ResponseEntity.ok(List.of(companyMapper.toDto(company)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{companyId}/games/{gameId}")
    @Transactional
    public ResponseEntity<CompanyDto> addGameToCompany(@PathVariable Long companyId,
                                                       @PathVariable Long gameId) {
        Company updatedCompany = companyService.addGameToCompany(companyId, gameId);
        if (updatedCompany != null) {
            if (updatedCompany.getGames() != null) {
                Hibernate.initialize(updatedCompany.getGames());
            }
            return ResponseEntity.ok(companyMapper.toDto(updatedCompany));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{companyId}/games/{gameId}")
    public ResponseEntity<Void> removeGameFromCompany(@PathVariable Long companyId,
                                                      @PathVariable Long gameId) {
        boolean removed = companyService.removeGameFromCompany(companyId, gameId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
