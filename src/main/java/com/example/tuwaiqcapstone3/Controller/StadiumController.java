package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stadium")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @GetMapping("/get")
    public ResponseEntity<?> getStadiums() {
        return ResponseEntity.status(200).body(stadiumService.getStadiums());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStadiumById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(stadiumService.getStadiumById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStadium(@RequestBody @Valid Stadium stadium) {
        stadiumService.addStadium(stadium);
        return ResponseEntity.status(200).body("stadium added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStadium(@PathVariable Integer id, @RequestBody @Valid Stadium stadium) {
        stadiumService.updateStadium(id, stadium);
        return ResponseEntity.status(200).body("stadium updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStadium(@PathVariable Integer id) {
        stadiumService.deleteStadium(id);
        return ResponseEntity.status(200).body("stadium deleted successfully");
    }
}
