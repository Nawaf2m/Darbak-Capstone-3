package com.example.tuwaiqcapstone3.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTO {

    @NotNull(message = "Ride id is required")
    private Integer rideId;

    @NotNull(message = "Passenger id is required")
    private Integer passengerId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 200, message = "Comment must be at most 200 characters")
    private String comment;
}
