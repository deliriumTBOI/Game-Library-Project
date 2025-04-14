package com.gamelib.gamelib.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;

    @NotNull(message = "Rating cannot be empty")
    @Min(value = 0, message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private Integer rating;

    @Size(max = 2000, message = "Review cannot exceed 2000 characters")
    private String text;

    @NotBlank(message = "Author name cannot be empty")
    @Size(min = 1, max = 200, message = "Author name cannot exceed 200 characters")
    private String author;

    private Long gameId;
}