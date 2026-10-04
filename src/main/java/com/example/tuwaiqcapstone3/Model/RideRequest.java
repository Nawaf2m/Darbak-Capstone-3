package com.example.tuwaiqcapstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Ride ride;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private User passenger;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDate requestedAt;

    @Pattern(regexp = "pending|accepted|rejected",message = "status must be ether pending or accepted or rejected")
    @Size(min = 3, max = 10, message = "status length must be between 3 and 10")
    private String status;
}
