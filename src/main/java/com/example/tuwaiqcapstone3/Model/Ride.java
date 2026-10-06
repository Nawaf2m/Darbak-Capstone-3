package com.example.tuwaiqcapstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private User driver;


    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Match match;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Car car;

    @OneToMany(mappedBy = "ride")
    private Set<RideRequest> rideRequests;

    @OneToMany(mappedBy = "ride")
    private Set<RidePerticipant> ridePerticipants;

    @NotNull(message = "departure date can not be null")
    private LocalDate departureDate;

    @NotNull(message = "departure time can not be null")
    private LocalTime departureTime;

    @NotEmpty(message = "meeting point can not be empty")
    @Size(min = 5, max = 255, message = "meeting point length must be between 5 and 255")
    @Column(columnDefinition = "varchar(255)",nullable = false)
    private String meetingPoint;

    @NotNull(message = "meeting latitude can not be null")
    private Double meetingLatitude;

    @NotNull(message = "meeting longitude can not be null")
    private Double meetingLongitude;

    @NotEmpty(message = "destination can not be empty")
    @Size(min = 5, max = 255, message = "destination length must be between 5 and 255")
    @Column(columnDefinition = "varchar(255)",nullable = false)
    private String destination;

    @Column(nullable = false)
    @NotNull(message = "available seats can not be null")
    @Min(value = 0, message = "available seats can not be negative")
    private Integer availableSeats;

    @Pattern(regexp = "available|full|completed|cancelled", message = "status must be either available or full or completed or cancelled")
    @Size(min = 3, max = 30, message = "status length must be between 3 and 30")
    @Column(columnDefinition = "varchar(30)",nullable = false)
    private String status;

    @NotNull(message = "expected arrival time can not be null")
    private LocalDateTime expectedArrivalTime;

    @NotEmpty(message = "notes can not be empty")
    @Size(min = 3, max = 500, message = "notes length must be between 3 and 500")
    @Column(columnDefinition = "varchar(500)",nullable = false)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDate creation_date;
}
