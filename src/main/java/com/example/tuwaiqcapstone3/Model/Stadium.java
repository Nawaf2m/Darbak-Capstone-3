package com.example.tuwaiqcapstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Stadium name is required")
    @Size(min = 2, max = 50, message = "Stadium name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotEmpty(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String city;

    @NotEmpty(message = "Address is required")
    @Size(max = 100, message = "Address must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String address;

    @NotNull(message = "Stadium latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @NotNull(message = "Stadium longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;
}
