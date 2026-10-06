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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "matches")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @NotNull(message = "Stadium is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @NotEmpty(message = "Home team is required")
    @Size(max = 100, message = "Home team must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String homeTeam;

    @NotEmpty(message = "Away team is required")
    @Size(max = 100, message = "Away team must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String awayTeam;

    @NotNull(message = "Match start time is required")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "Expected end time is required")
    @Column(nullable = false)
    private LocalDateTime expectedEndTime;

    @NotEmpty(message = "Match status is required")
    @Pattern(regexp = "scheduled|live|completed|postponed|cancelled", message = "Status must be scheduled, live, completed, postponed, or cancelled")
    @Column(nullable = false, length = 20)
    private String status;

    @Column(unique = true)
    private Integer externalFixtureId;

    private Integer season;

    @Column(length = 100)
    private String round;

    private Integer homeTeamExternalId;

    private Integer awayTeamExternalId;

    @Column(length = 20)
    private String externalStatus;
}
