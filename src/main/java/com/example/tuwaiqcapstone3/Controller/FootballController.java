package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.API.ApiResponse;
import com.example.tuwaiqcapstone3.Service.FootballService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/football")
@RequiredArgsConstructor
public class FootballController {

    private final FootballService footballService;

    @PostMapping("/import-test-matches")
    public ResponseEntity<?> importTestMatches() {
        Integer count = footballService.importTestMatches();
        return ResponseEntity.status(200).body(new ApiResponse("test matches imported successfully: " + count));
    }
}