package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewSummaryDTO {

    private Integer userId;

    private Integer reviewCount;

    private Double averageRating;

    private String summary;
}