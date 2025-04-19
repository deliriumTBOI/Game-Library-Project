package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.mapper.CompanyMapper;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Companies", description = "API для управления компаниями")
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    public CompanyController(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Получить список компаний", description = "Возвращает список всех компаний"
            + " или компаний, соответствующих указанному названию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список компаний успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class)))
    })
    public ResponseEntity<List<CompanyDto>>
        getAllCompanies(
                @Parameter(description = "Название компании (опционально)")
                @RequestParam(value = "name", required = false) String name) {
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
    @Operation(summary = "Получить компанию по ID", description = "Возвращает компанию "
            + "по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Компания успешно найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content)
    })
    public ResponseEntity<CompanyDto> getCompanyById(
            @Parameter(description = "ID компании", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Создать новую компанию", description = "Создает новую компанию "
            + "с указанными данными")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Компания успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные "
                + "для создания компании",
                    content = @Content),
        @ApiResponse(responseCode = "409", description = "Компания с таким названием "
                + "уже существует",
                    content = @Content)
    })
    public ResponseEntity<Object> createCompany(
            @Parameter(description = "Данные новой компании", required = true)
            @Valid @RequestBody CompanyDto companyDto) {
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

    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько компаний", description = "Создает список "
            + "новых компаний")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Компании успешно созданы",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "400", description = "Некорректные данные "
                + "для создания компаний",
                    content = @Content)
    })
    public ResponseEntity<List<CompanyDto>> createCompanies(
            @Valid @RequestBody List<@Valid CompanyDto> companyDtos) {
        List<Company> companies = companyDtos.stream()
                .map(companyMapper::toEntity)
                .toList();

        List<Company> createdCompanies = companyService.createCompanies(companies);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyMapper.toDtoList(createdCompanies));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Обновить компанию", description = "Обновляет компанию по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Компания успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Некорректные данные "
                + "для обновления компании",
                    content = @Content)
    })
    public ResponseEntity<CompanyDto> updateCompany(
            @Parameter(description = "ID компании", required = true) @PathVariable Long id,
            @Parameter(description = "Обновленные данные компании", required = true)
            @Valid @RequestBody CompanyDto companyDto) {
        Company company = companyMapper.toEntity(companyDto);
        Company updatedCompany = companyService.updateCompany(id, company);

        if (updatedCompany != null && updatedCompany.getGames() != null) {
            Hibernate.initialize(updatedCompany.getGames());
        }

        return updatedCompany != null ? ResponseEntity.ok(companyMapper.toDto(updatedCompany)) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить компанию", description = "Удаляет компанию по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Компания успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Компания не найдена")
    })
    public ResponseEntity<Object> deleteCompany(
            @Parameter(description = "ID компании для удаления", required = true)
            @PathVariable Long id) {
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
    @Operation(summary = "Получить игры компании", description = "Возвращает список игр, "
            + "связанных с указанной компанией")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список игр компании успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content)
    })
    public ResponseEntity<List<CompanyDto>> getGamesForCompany(
            @Parameter(description = "ID компании", required = true) @PathVariable Long companyId) {
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
    @Operation(summary = "Добавить игру к компании", description = "Добавляет связь "
            + "между компанией и игрой")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Игра успешно добавлена к компании",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDto.class))),
        @ApiResponse(responseCode = "404", description = "Компания или игра не найдена",
                    content = @Content)
    })
    public ResponseEntity<CompanyDto> addGameToCompany(
            @Parameter(description = "ID компании", required = true) @PathVariable Long companyId,
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId) {
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
    @Operation(summary = "Удалить игру из компании", description = "Удаляет связь "
            + "между компанией и игрой")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Связь успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Компания или игра не найдена",
                    content = @Content)
    })
    public ResponseEntity<Void> removeGameFromCompany(
            @Parameter(description = "ID компании", required = true) @PathVariable Long companyId,
            @Parameter(description = "ID игры", required = true) @PathVariable Long gameId) {
        boolean removed = companyService.removeGameFromCompany(companyId, gameId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}