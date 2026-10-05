package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RideRequest;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.RideRequestRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public List<RideRequest> getRideRequests(){
        List<RideRequest> rideRequests = rideRequestRepository.findAll();
        if(rideRequests.isEmpty()){
            throw new ApiException("there is no requests in the system");
        }
        return rideRequests;
    }

    public void addRideRequest(RideRequest rideRequest){

        Ride ride = rideRepository.findRideById(rideRequest.getRide().getId());

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(rideRequest.getPassenger().getId());

        if(user==null){
            throw new ApiException("passenger id not found");
        }

        rideRequest.setStatus("pending");
        rideRequestRepository.save(rideRequest);
    }

    public void updateRideRequest(Integer id, RideRequest rideRequest){
        RideRequest oldRideRequest = rideRequestRepository.findRideRequestById(id);

        if(oldRideRequest==null){
            throw new ApiException("ride request id not found");
        }

        Ride ride = rideRepository.findRideById(rideRequest.getRide().getId());

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(rideRequest.getPassenger().getId());

        if(user==null){
            throw new ApiException("passenger id not found");
        }

        oldRideRequest.setRide(rideRequest.getRide());
        oldRideRequest.setPassenger(rideRequest.getPassenger());
        oldRideRequest.setStatus(rideRequest.getStatus());

        rideRequestRepository.save(oldRideRequest);
    }

    public void deleteRideRequest(Integer id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(id);

        if(rideRequest==null){
            throw new ApiException("ride request id not found");
        }

        rideRequestRepository.delete(rideRequest);
    }
}