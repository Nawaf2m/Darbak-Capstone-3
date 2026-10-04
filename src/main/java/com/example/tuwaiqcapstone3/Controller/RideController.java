package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Ride")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping("/get")
    public ResponseEntity<?> getRides(){
        return ResponseEntity.status(200).body(rideService.getRides());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRide(@RequestBody @Valid Ride ride){
        rideService.addRide(ride);

        return ResponseEntity.status(200).body("ride added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRide(@PathVariable Integer id, @RequestBody @Valid Ride ride){
        rideService.updateRide(id,ride);

        return ResponseEntity.status(200).body("ride updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable Integer id){
        rideService.deleteRide(id);

        return ResponseEntity.status(200).body("ride deleted successfully");
    }
}