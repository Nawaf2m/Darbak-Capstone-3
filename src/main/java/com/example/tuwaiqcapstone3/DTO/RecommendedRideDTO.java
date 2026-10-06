package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class RecommendedRideDTO {
    private Integer rideId;
    private Integer matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime matchStartTime;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDateTime expectedArrivalTime;
    private String meetingPoint;
    private Double meetingLatitude;
    private Double meetingLongitude;
    private Integer availableSeats;
}
