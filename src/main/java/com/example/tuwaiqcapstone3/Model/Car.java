package com.example.tuwaiqcapstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private User driver;

    @NotEmpty(message = "car name can not be empty")
    @Length(min = 2, max = 15, message = "car name length must be between 2 and 15")
    @Column(columnDefinition = "varchar(15)", nullable = false)
    private String carName;

    @NotEmpty(message = "plate number can not be empty")
    @Length(min = 4, max = 4, message = "plate number must be 4 characters")
    @Column(columnDefinition = "varchar(4)", nullable = false, unique = true)
    private String plateNumber;

    @NotNull(message = "seats count can not be null")
    @Min(value = 1, message = "seats count must be at least 1")
    @Column(nullable = false)
    private Integer seatsCount;

    @NotEmpty(message = "color can not be empty")
    @Length(min = 2, max = 10, message = "color length must be between 2 and 10")
    @Column(columnDefinition = "varchar(10)", nullable = false)
    private String color;

    @OneToMany(mappedBy = "car")
    @JsonIgnore
    private Set<Ride> rides;
}