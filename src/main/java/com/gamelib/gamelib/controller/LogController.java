package com.gamelib.gamelib.controller;

import com.gamelib.gamelib.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Controller", description = "API for accessing application logs")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    @Operation(summary = "Get logs by date", description = "Retrieves application "
            + "logs for a specific date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved logs",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "No logs found for the specified date",
                    content = @Content)
    })
    public ResponseEntity<Resource> getLogsByDate(
            @Parameter(description = "Date in ISO format (yyyy-MM-dd)")
            @RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        Resource logResource = logService.getLogFileByDate(date);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"gamelib-" + dateStr + ".log\"")
                .body(logResource);
    }
}