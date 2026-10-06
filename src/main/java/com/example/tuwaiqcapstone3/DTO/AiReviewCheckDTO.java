package com.example.tuwaiqcapstone3.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiReviewCheckDTO {

    private Boolean inappropriate;
    private String reason;
}
