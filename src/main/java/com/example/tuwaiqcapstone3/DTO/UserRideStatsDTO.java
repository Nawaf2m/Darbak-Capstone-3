package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserRideStatsDTO {
    private Integer totalRidesAsDriver;
    private Integer completedRidesAsDriver;
    private Integer cancelledRidesAsDriver;
    private Integer totalRidesAsPassenger;
    private Integer completedRidesAsPassenger;


}
