package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.CarRepository;
import com.example.tuwaiqcapstone3.Repository.MatchRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final CarRepository carRepository;

    public List<Ride> getRides(){
        List<Ride> rides = rideRepository.findAll();

        if(rides.isEmpty()){
            throw new ApiException("there is no rides in the system");
        }
        return rides;
    }

    public void addRide(Integer driver_id,Integer match_id,Integer car_id,Ride ride){

        User driver = userRepository.findUserById(driver_id);

        if(driver==null){
            throw new ApiException("driver id not found");
        }

        Match match = matchRepository.findMatchById(match_id);

        if(match==null){
            throw new ApiException("match id not found");
        }

        Car car = carRepository.findCarById(car_id);

        if(car==null){
            throw new ApiException("car id not found");
        }

        boolean found=false;
        Set<Car> cars = driver.getCars();
        for(Car i :cars){
            if(i.getId().equals(car.getId())){
                found =true;
                break;
            }
        }
        if(!found){
            throw new ApiException("car dont belong to user");
        }
        if(ride.getAvailableSeats() > car.getSeatsCount()){
            throw new ApiException("available seats cannot exceed car capacity");
        }

        ride.setDriver(driver);
        ride.setMatch(match);
        ride.setCar(car);
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

        if(ride.getAvailableSeats() > oldRide.getCar().getSeatsCount()){
            throw new ApiException("available seats cannot exceed car capacity");
        }
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