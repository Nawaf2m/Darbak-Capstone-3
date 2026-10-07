package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AiMatchPlanDTO {

    private boolean possibility;

    private String recommendation;

    private List<String> advice;

    private Integer estimatedArrivalMinutes;

}
