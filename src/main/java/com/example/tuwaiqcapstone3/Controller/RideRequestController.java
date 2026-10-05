package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.RideRequest;
import com.example.tuwaiqcapstone3.Service.RideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/RideRequest")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    @GetMapping("/get")
    public ResponseEntity<?> getRideRequests(){
        return ResponseEntity.status(200).body(rideRequestService.getRideRequests());
    }

    @PostMapping("/add/{user_id}/{ride_id}")
    public ResponseEntity<?> addRideRequest(@PathVariable Integer user_id,@PathVariable Integer ride_id,@RequestBody @Valid RideRequest rideRequest){
        rideRequestService.addRideRequest(user_id, ride_id, rideRequest);

        return ResponseEntity.status(200).body("ride request added successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRideRequest(@PathVariable Integer id, @RequestBody @Valid RideRequest rideRequest){
        rideRequestService.updateRideRequest(id,rideRequest);

        return ResponseEntity.status(200).body("ride request updated successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRideRequest(@PathVariable Integer id){
        rideRequestService.deleteRideRequest(id);

        return ResponseEntity.status(200).body("ride request deleted successfully");
    }
}