package com.gamelib.gamelib.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 250, message = "Name cannot exceed 250 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Integer foundedYear;

    @Size(max = 1000, message = "URL cannot exceed 1000 characters")
    private String website;

    private Set<String> gameNames = new HashSet<>();
}