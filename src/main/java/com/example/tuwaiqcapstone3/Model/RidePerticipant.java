package com.example.tuwaiqcapstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RidePerticipant {

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
    private User user;

    @Column(nullable = false)
    @NotEmpty(message = "role can not be empty")
    @Size(min = 3, max = 10, message = "role length must be between 3 and 10")
    private String role;

    @Column(nullable = false)
    @NotNull(message = "joined at can not be null")
    private LocalDate joinedAt;
}
