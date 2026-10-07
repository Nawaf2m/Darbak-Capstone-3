package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RideMapDetailsDTO {
    private Integer rideId;
    private Integer matchId;
    private String meetingPoint;
    private Double meetingLatitude;
    private Double meetingLongitude;
    private Integer stadiumId;
    private String stadiumName;
    private BigDecimal stadiumLatitude;
    private BigDecimal stadiumLongitude;
    private String meetingDirectionsUrl;
    private String stadiumDirectionsUrl;
    private String rideDirectionsUrl;
}
