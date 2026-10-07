package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RideRequest;
import com.example.tuwaiqcapstone3.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest,Integer> {
    RideRequest findRideRequestById(Integer id);

    List<RideRequest> findRideRequestByRide(Ride ride);

    List<RideRequest> findRideRequestByPassenger(User user);
    List<RideRequest> findRideRequestByPassengerAndStatus(User user,String status);

    List<RideRequest> findRideRequestByRideAndPassenger(Ride ride, User passenger);
}
