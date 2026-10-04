package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Service.RidePerticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/RidePerticipant")
@RequiredArgsConstructor
public class RidePerticipantController {

    private final RidePerticipantService ridePerticipantService;

    @GetMapping("/get")
    public ResponseEntity<?> getRidePerticipants(){
        return ResponseEntity.status(200).body(ridePerticipantService.getRidePerticipants());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRidePerticipant(@RequestBody @Valid RidePerticipant ridePerticipant){
        ridePerticipantService.addRidePerticipant(ridePerticipant);

        return ResponseEntity.status(200).body("ride participant added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRidePerticipant(@PathVariable Integer id, @RequestBody @Valid RidePerticipant ridePerticipant){
        ridePerticipantService.updateRidePerticipant(id,ridePerticipant);

        return ResponseEntity.status(200).body("ride participant updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRidePerticipant(@PathVariable Integer id){
        ridePerticipantService.deleteRidePerticipant(id);

        return ResponseEntity.status(200).body("ride participant deleted successfully");
    }
}