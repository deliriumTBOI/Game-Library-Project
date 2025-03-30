package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.mapper.CompanyMapper;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.service.CompanyService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<CompanyDto> getAllCompaniesWithGames(@RequestParam(
            value = "name", required = false) String name) {
        if (name != null) {
            return companyService.getCompaniesByNameWithGames(name);
        } else {
            return companyService.getAllCompaniesWithGames();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyByIdWithGames(@PathVariable Long id) {
        return companyService.getCompanyByIdWithGames(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Добавить новую компанию
    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody CompanyDto companyDto) {
        Company createdCompany = companyService.createCompany(companyDto);

        if (createdCompany == null) {
            // Возвращаем 409 Conflict с текстом ошибки
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Company is already exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(CompanyMapper.toDto(createdCompany));
    }


    // Обновить компанию
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id,
                                                    @RequestBody CompanyDto companyDto) {
        Company updatedCompany = companyService.updateCompany(id, companyDto);
        return updatedCompany != null ? ResponseEntity.ok(new CompanyDto(updatedCompany)) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        boolean isDeleted = companyService.deleteCompany(id);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company with ID " + id + " not found");
        }
    }

}
