package com.example.tuwaiqcapstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_matches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "match_id"}))
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "User is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Match is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
}
