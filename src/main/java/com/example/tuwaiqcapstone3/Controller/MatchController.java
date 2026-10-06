package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/get")
    public ResponseEntity<?> getMatches() {
        return ResponseEntity.status(200).body(matchService.getMatches());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(matchService.getMatchById(id));
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<?> getMatchesByStadiumId(@PathVariable Integer stadiumId) {
        return ResponseEntity.status(200).body(matchService.getMatchesByStadiumId(stadiumId));
    }

    @GetMapping("/team/{teamName}")
    public ResponseEntity<?> getMatchesByTeam(@PathVariable String teamName) {
        return ResponseEntity.status(200).body(matchService.getMatchesByTeam(teamName));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<?> getMatchesByCity(@PathVariable String city) {
        return ResponseEntity.status(200).body(matchService.getMatchesByCity(city));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMatch(@RequestBody @Valid Match match) {
        matchService.addMatch(match);
        return ResponseEntity.status(200).body("match added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable Integer id, @RequestBody @Valid Match match) {
        matchService.updateMatch(id, match);
        return ResponseEntity.status(200).body("match updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Integer id) {
        matchService.deleteMatch(id);
        return ResponseEntity.status(200).body("match deleted successfully");
    }
}
