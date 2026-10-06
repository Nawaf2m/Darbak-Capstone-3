package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
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

    public List<RidePerticipant> getRidePerticipants() {
        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findAll();

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("there is no ride participants");
        }

        return ridePerticipants;
    }

    public void addRidePerticipant(Integer ride_id, Integer user_id, RidePerticipant ridePerticipant) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(user_id);

        if (user == null) {
            throw new ApiException("user id not found");
        }

        ridePerticipant.setRide(ride);
        ridePerticipant.setUser(user);
        ridePerticipantRepository.save(ridePerticipant);
    }

    public void updateRidePerticipant(Integer id, RidePerticipant ridePerticipant) {
        RidePerticipant oldRidePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if (oldRidePerticipant == null) {
            throw new ApiException("ride participant id not found");
        }

        Ride ride = rideRepository.findRideById(ridePerticipant.getRide().getId());

        if (ride == null) {
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(ridePerticipant.getUser().getId());

        if (user == null) {
            throw new ApiException("user id not found");
        }

        oldRidePerticipant.setRole(ridePerticipant.getRole());
        oldRidePerticipant.setJoinedAt(ridePerticipant.getJoinedAt());

        ridePerticipantRepository.save(oldRidePerticipant);
    }

    public void deleteRidePerticipant(Integer id) {
        RidePerticipant ridePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if (ridePerticipant == null) {
            throw new ApiException("ride participant id not found");
        }

        ridePerticipantRepository.delete(ridePerticipant);
    }

    public RidePerticipant getSpecificRidePerticipant(Integer user_id, Integer ride_id) {
        User user = userRepository.findUserById(user_id);

        if (user == null) {
            throw new ApiException("user not found");
        }

        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        RidePerticipant ridePerticipant = ridePerticipantRepository.findRidePerticipantByUserAndRide(user, ride);

        if (ridePerticipant == null) {
            throw new ApiException("ride participant not found");
        }

        return ridePerticipant;
    }

    public List<RidePerticipant> GetAllParticipantsInARide(Integer ride_id) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findRidePerticipantByRide(ride);

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("ride dont have participant");
        }

        return ridePerticipants;
    }

    public List<RidePerticipant> GetParticipantsByRoleInARide(Integer ride_id, String role) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findRidePerticipantByRideAndRole(ride, role);

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("there is no participant with this role");
        }

        return ridePerticipants;
    }

    public Long getPassengerCount(Integer rideId) {
        Ride ride = rideRepository.findRideById(rideId);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        return ridePerticipantRepository.countPassengersByRideId(rideId);
    }
}