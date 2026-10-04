package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.UserMatch;
import com.example.tuwaiqcapstone3.Service.UserMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-match")
@RequiredArgsConstructor
public class UserMatchController {

    private final UserMatchService userMatchService;

    @GetMapping("/get")
    public ResponseEntity<?> getUserMatches() {
        return ResponseEntity.status(200).body(userMatchService.getUserMatches());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserMatchById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(userMatchService.getUserMatchById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUserMatch(@RequestBody @Valid UserMatch userMatch) {
        userMatchService.addUserMatch(userMatch);
        return ResponseEntity.status(200).body("user match added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserMatch(@PathVariable Integer id, @RequestBody @Valid UserMatch userMatch) {
        userMatchService.updateUserMatch(id, userMatch);
        return ResponseEntity.status(200).body("user match updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserMatch(@PathVariable Integer id) {
        userMatchService.deleteUserMatch(id);
        return ResponseEntity.status(200).body("user match deleted successfully");
    }

}
