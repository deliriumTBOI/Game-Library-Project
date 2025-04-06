package com.gamelib.gamelib.dto;

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
    private String title;
    private String description;
    private Date releaseDate;
    private String genre;
    private Set<String> companyNames = new HashSet<>();
    private List<ReviewDto> reviews = new ArrayList<>();
}