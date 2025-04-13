package com.gamelib.gamelib.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 250, message = "Title cannot exceed 250 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Date releaseDate;

    @Size(max = 100, message = "Genre cannot exceed 100 characters")
    private String genre;

    private Set<String> companyNames = new HashSet<>();

    private List<ReviewDto> reviews = new ArrayList<>();
}