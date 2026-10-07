package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiMatchdayPlanDTO {

    private Integer userId;

    private Integer upcomingRidesCount;

    private Integer matchesWithoutRidesCount;

    private String plan;
}