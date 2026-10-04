package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Repository.CarRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final CarRepository carRepository;

    public List<Ride> getRides(){
        return rideRepository.findAll();
    }

    public void addRide(Ride ride){

        User user = userRepository.findUserById(ride.getDriver().getId());

        if(user==null){
            throw new ApiException("driver id not found");
        }

        Match match = matchRepository.findMatchById(ride.getMatch().getId());

        if(match==null){
            throw new ApiException("match id not found");
        }

        Car car = carRepository.findCarById(ride.getCar().getId());

        if(car==null){
            throw new ApiException("car id not found");
        }

        ride.setStatus("available");
        rideRepository.save(ride);
    }

    public void updateRide(Integer id, Ride ride){
        Ride oldRide = rideRepository.findRideById(id);

        if(oldRide==null){
            throw new ApiException("ride id not found");
        }

        oldRide.setDepartureDate(ride.getDepartureDate());
        oldRide.setDepartureTime(ride.getDepartureTime());
        oldRide.setMeetingPoint(ride.getMeetingPoint());
        oldRide.setMeetingLatitude(ride.getMeetingLatitude());
        oldRide.setMeetingLongitude(ride.getMeetingLongitude());
        oldRide.setDestination(ride.getDestination());
        oldRide.setAvailableSeats(ride.getAvailableSeats());
        oldRide.setNotes(ride.getNotes());

        rideRepository.save(oldRide);
    }

    public void deleteRide(Integer id){
        Ride ride = rideRepository.findRideById(id);
        if(ride==null){
            throw new ApiException("ride id not found");
        }

        rideRepository.delete(ride);
    }
}