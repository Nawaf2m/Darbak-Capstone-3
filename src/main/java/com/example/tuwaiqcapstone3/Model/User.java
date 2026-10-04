package com.example.tuwaiqcapstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "name can not be empty")
    @Length(min = 2, max = 15, message = "name length must be between 2 and 15")
    @Column(columnDefinition = "varchar(15)", nullable = false)
    private String name;

    @NotEmpty(message = "email can not be empty")
    @Email(message = "email must be valid")
    @Column(columnDefinition = "varchar(40)", nullable = false, unique = true)
    private String email;

    @NotEmpty(message = "password can not be empty")
    @Length(min = 6, message = "password must be at least 6 characters")
    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String password;

    @NotEmpty(message = "phone number can not be empty")
    @Pattern(
            regexp = "^05[0-9]{8}$",
            message = "phone number must start with 05 and contain 10 digits"
    )
    @Column(columnDefinition = "varchar(10)", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "driver")
    @JsonIgnore
    private Set<Car> cars;


    @OneToMany(mappedBy = "driver")
    @JsonIgnore
    private Set<Ride> rides;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<RidePerticipant> ridePerticipants;


    @OneToMany(mappedBy = "passenger")
    @JsonIgnore
    private Set<RideRequest> rideRequests;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserMatch> userMatches;


    @OneToMany(mappedBy = "reviewer")
    @JsonIgnore
    private Set<Review> reviewsWritten;


    @OneToMany(mappedBy = "reviewedUser")
    @JsonIgnore
    private Set<Review> reviewsReceived;
}