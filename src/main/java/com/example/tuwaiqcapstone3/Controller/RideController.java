package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.API.ApiResponse;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/Ride")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping("/get")
    public ResponseEntity<?> getRides() {
        return ResponseEntity.status(200).body(rideService.getRides());
    }

    @PostMapping("/add/{driverId}/{matchId}/{carId}")
    public ResponseEntity<?> addRide(@PathVariable Integer driverId, @PathVariable Integer matchId, @PathVariable Integer carId, @RequestBody @Valid Ride ride) {
        rideService.addRide(driverId, matchId, carId, ride);
        return ResponseEntity.status(200).body(new ApiResponse("ride added successfully"));
    }

    @PutMapping("/update/{id}/{driverId}")
    public ResponseEntity<?> updateRide(@PathVariable Integer id, @PathVariable Integer driverId, @RequestBody @Valid Ride ride) {
        rideService.updateRide(id, driverId, ride);
        return ResponseEntity.status(200).body(new ApiResponse("ride updated successfully"));
    }

    @DeleteMapping("/delete/{id}/{driverId}")
    public ResponseEntity<?> deleteRide(@PathVariable Integer id, @PathVariable Integer driverId) {
        rideService.deleteRide(id, driverId);
        return ResponseEntity.status(200).body(new ApiResponse("ride deleted successfully"));
    }

    @GetMapping("/search/{matchId}/{date}/{seats}")
    public ResponseEntity<?> getSuitableRides(@PathVariable Integer matchId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @PathVariable Integer seats) {
        return ResponseEntity.status(200).body(rideService.getSuitableRides(matchId, date, seats));
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRides() {
        return ResponseEntity.status(200).body(rideService.getAvailableRides());
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getRidesByMatchId(@PathVariable Integer matchId) {
        return ResponseEntity.status(200).body(rideService.getRidesByMatchId(matchId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<?> getRidesByDriverId(@PathVariable Integer driverId) {
        return ResponseEntity.status(200).body(rideService.getRidesByDriverId(driverId));
    }

    @PutMapping("/complete/{rideId}/{driverId}")
    public ResponseEntity<?> completeRide(@PathVariable Integer rideId, @PathVariable Integer driverId) {
        rideService.completeRide(rideId, driverId);
        return ResponseEntity.status(200).body(new ApiResponse("ride completed successfully"));
    }

    @PutMapping("/cancel/{rideId}/{driverId}")
    public ResponseEntity<?> cancelRide(@PathVariable Integer rideId, @PathVariable Integer driverId) {
        rideService.cancelRide(rideId, driverId);
        return ResponseEntity.status(200).body(new ApiResponse("ride cancelled successfully"));
    }
}