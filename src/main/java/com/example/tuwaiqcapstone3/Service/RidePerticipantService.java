package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.api.ApiException;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.RidePerticipantRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RidePerticipantService {

    private final RidePerticipantRepository ridePerticipantRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public List<RidePerticipant> getRidePerticipants(){
        return ridePerticipantRepository.findAll();
    }

    public void addRidePerticipant(RidePerticipant ridePerticipant){

        Ride ride = rideRepository.findRideById(ridePerticipant.getRide().getId());

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(ridePerticipant.getUser().getId());

        if(user==null){
            throw new ApiException("user id not found");
        }

        ridePerticipantRepository.save(ridePerticipant);
    }

    public void updateRidePerticipant(Integer id, RidePerticipant ridePerticipant){
        RidePerticipant oldRidePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if(oldRidePerticipant==null){
            throw new ApiException("ride participant id not found");
        }

        Ride ride = rideRepository.findRideById(ridePerticipant.getRide().getId());

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(ridePerticipant.getUser().getId());

        if(user==null){
            throw new ApiException("user id not found");
        }

        oldRidePerticipant.setRole(ridePerticipant.getRole());
        oldRidePerticipant.setJoinedAt(ridePerticipant.getJoinedAt());

        ridePerticipantRepository.save(oldRidePerticipant);
    }

    public void deleteRidePerticipant(Integer id){
        RidePerticipant ridePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if(ridePerticipant==null){
            throw new ApiException("ride participant id not found");
        }

        ridePerticipantRepository.delete(ridePerticipant);
    }
}