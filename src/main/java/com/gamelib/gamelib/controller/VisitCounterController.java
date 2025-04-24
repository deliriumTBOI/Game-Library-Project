package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.service.VisitCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
@Tag(name = "Visit Counter", description = "API для учета посещений различных URL в приложении")
public class VisitCounterController {
    private final VisitCounterService visitCounterService;

    @GetMapping("/count")
    @Operation(summary = "Получить количество посещений конкретного URL",
            description = "Возвращает количество посещений для заданного URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Количество посещений успешно получено")
    })
    public ResponseEntity<Integer> getVisitCount(
            @Parameter(description = "URL, для которого нужно получить количество посещений",
                    required = true)
            @RequestParam String url) {
        return ResponseEntity.ok(visitCounterService.getVisitCount(url));
    }

    @GetMapping("/total")
    @Operation(summary = "Получить общее количество посещений",
            description = "Возвращает общее количество посещений всех URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "Общее количество посещений успешно получено")
    })
    public ResponseEntity<Map<String, Integer>> getTotalVisitCount() {
        return ResponseEntity.ok(
                Map.of("total", visitCounterService.getTotalVisitCount())
        );
    }

    @GetMapping("/all")
    @Operation(summary = "Получить статистику по всем URL",
            description = "Возвращает карту URL и количества посещений для каждого из них")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика по всем URL успешно получена")
    })
    public ResponseEntity<Map<String, Integer>> getAllVisitCounts() {
        return ResponseEntity.ok(visitCounterService.getAllVisitCounts());
    }
}
