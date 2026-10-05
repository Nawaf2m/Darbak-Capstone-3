package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RidePerticipantRepository extends JpaRepository<RidePerticipant,Integer> {
    RidePerticipant findRidePerticipantById(Integer id);

    boolean existsByRideIdAndUserId(Integer rideId, Integer userId);

    RidePerticipant findRidePerticipantByUserAndRide(User user, Ride ride);

    List<RidePerticipant> findRidePerticipantByRide(Ride ride);

    List<RidePerticipant> findRidePerticipantByRideAndRole(Ride ride, String role);

    // rides the user joined as a passenger (excluding rides where he is the driver)
    @Query("select count(p) from RidePerticipant p " +
            "where p.user.id = :userId and p.ride.driver.id <> :userId")
    Integer countRidesAsPassenger(Integer userId);

    @Query("select count(p) from RidePerticipant p " +
            "where p.user.id = :userId and p.ride.driver.id <> :userId " +
            "and p.ride.status = :status")
    Integer countRidesAsPassengerByStatus(Integer userId, String status);
//long if error
}
