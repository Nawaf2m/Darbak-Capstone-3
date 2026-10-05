package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Service.RidePerticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/RidePerticipant")
@RequiredArgsConstructor
public class RidePerticipantController {

    private final RidePerticipantService ridePerticipantService;

    @GetMapping("/get")
    public ResponseEntity<?> getRidePerticipants(){
        return ResponseEntity.status(200).body(ridePerticipantService.getRidePerticipants());
    }

    @PostMapping("/add/{ride_id}/{user_id}")
    public ResponseEntity<?> addRidePerticipant(@PathVariable Integer ride_id ,@PathVariable Integer user_id ,@RequestBody @Valid RidePerticipant ridePerticipant){
        ridePerticipantService.addRidePerticipant(ride_id,user_id,ridePerticipant);

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
    @GetMapping("/user/{user_id}/ride/{ride_id}")
    public ResponseEntity<?> getSpecificRidePerticipant(@PathVariable Integer user_id,@PathVariable Integer ride_id){
        RidePerticipant ridePerticipant = ridePerticipantService.getSpecificRidePerticipant(user_id,ride_id);

        return ResponseEntity.status(200).body(ridePerticipant);
    }

    @GetMapping("/ride/{ride_id}")
    public ResponseEntity<?> GetAllParticipantsInARide(@PathVariable Integer ride_id){
        List<RidePerticipant> ridePerticipants = ridePerticipantService.GetAllParticipantsInARide(ride_id);

        return ResponseEntity.status(200).body(ridePerticipants);
    }

    @GetMapping("/ride/{ride_id}/role/{role}")
    public ResponseEntity<?> GetParticipantsByRoleInARideInteger(@PathVariable Integer ride_id,@PathVariable String role){
        List<RidePerticipant> ridePerticipants = ridePerticipantService.GetParticipantsByRoleInARideInteger(ride_id,role);

        return ResponseEntity.status(200).body(ridePerticipants);
    }
}