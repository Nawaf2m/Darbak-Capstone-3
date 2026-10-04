package com.example.tuwaiqcapstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotEmpty(message = "Admin name is required")
    @Size(min = 4, max = 20, message = "Name must be between 4 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = " Name must contain only characters ")
    @Column(columnDefinition = "varchar(20) not null")
    private String name;


    @NotEmpty(message = "Email is required")
    @Email
    @Size(max = 50, message = "Email must not exceed 50 characters")
    @Column(columnDefinition = "varchar(50) not null unique")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "Password must contain at least one letter and one number"
    )
    @Column(columnDefinition = "varchar(255) not null")
    private String password;


    @NotEmpty(message = "Phone is required")
    @Pattern(
            regexp = "^\\+9665[0-9]{8}$",
            message = "Invalid Saudi mobile number. Must match format: +9665XXXXXXXX"
    )
    @Column(columnDefinition = "varchar(15) not null unique")
    private String phone;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}
