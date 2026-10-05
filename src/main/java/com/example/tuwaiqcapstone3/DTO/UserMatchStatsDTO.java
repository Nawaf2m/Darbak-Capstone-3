package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMatchStatsDTO {
    private Integer totalMatches;
    private Integer upcomingMatches;
    private Integer pastMatches;

}
