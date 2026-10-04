package com.example.tuwaiqcapstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(columnDefinition = " int not null")
    private Integer rating;

    @Size(max = 200, message = "Comment must be at most 200 characters")
    @Column(columnDefinition = "varchar(200) ")
    private String comment;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn
    private User reviewer;

    @ManyToOne
    @JoinColumn
    private User reviewedUser;

    @ManyToOne
    @JoinColumn
    private Ride ride;
}
