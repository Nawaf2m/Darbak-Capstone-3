package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.Model.RideRequest;
import com.example.tuwaiqcapstone3.Service.RideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/ride/{ride_id}")
    public ResponseEntity<?> ViewRequestsForARide(@PathVariable Integer ride_id){
        List<RideRequest> rideRequests = rideRequestService.ViewRequestsForARide(ride_id);

        return ResponseEntity.status(200).body(rideRequests);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> ViewRequestsForAUser(@PathVariable Integer user_id){
        List<RideRequest> rideRequests = rideRequestService.ViewRequestsForAUser(user_id);

        return ResponseEntity.status(200).body(rideRequests);
    }

    @PostMapping("/accept/{request_id}/{driver_id}")
    public ResponseEntity<?> AcceptRequest(@PathVariable Integer request_id,@PathVariable Integer driver_id){
        rideRequestService.AcceptRequest(request_id,driver_id);

        return ResponseEntity.status(200).body("request accepted successfully");
    }

    @PostMapping("/reject/{request_id}/{driver_id}")
    public ResponseEntity<?> RejectRequest(@PathVariable Integer request_id,@PathVariable Integer driver_id){
        rideRequestService.RejectRequest(request_id,driver_id);

        return ResponseEntity.status(200).body("request rejected successfully");
    }

    @DeleteMapping("/cancel/{request_id}/{passenger_id}")
    public ResponseEntity<?> CancelRequest(@PathVariable Integer request_id,@PathVariable Integer passenger_id){
        rideRequestService.CancelRequest(request_id,passenger_id);

        return ResponseEntity.status(200).body("request cancelled successfully");
    }

    @GetMapping("/user/{user_id}/pending")
    public ResponseEntity<?> getUserPendingRequest(@PathVariable Integer user_id){
        List<RideRequest> rideRequests = rideRequestService.getUserPendingRequest(user_id);

        return ResponseEntity.status(200).body(rideRequests);
    }
}