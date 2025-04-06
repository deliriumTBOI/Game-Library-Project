package com.gamelib.gamelib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Integer rating;
    private String text;
    private String author;
    private Long gameId;
}
