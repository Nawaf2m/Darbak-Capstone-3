package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RidePerticipantRepository extends JpaRepository<RidePerticipant, Integer> {

    RidePerticipant findRidePerticipantById(Integer id);

    boolean existsByRideIdAndUserId(Integer rideId, Integer userId);

    boolean existsByRideIdAndUserIdAndRole(Integer rideId, Integer userId, String role);

    RidePerticipant findRidePerticipantByUserAndRide(User user, Ride ride);

    List<RidePerticipant> findRidePerticipantByRide(Ride ride);

    List<RidePerticipant> findRidePerticipantByRideAndRole(Ride ride, String role);

    @Query("select count(p) from RidePerticipant p where p.user.id = ?1 and p.ride.driver.id <> ?1")
    Integer countRidesAsPassenger(Integer userId);

    @Query("select count(p) from RidePerticipant p where p.user.id = ?1 and p.ride.driver.id <> ?1 and p.ride.status = ?2")
    Integer countRidesAsPassengerByStatus(Integer userId, String status);

    @Query("select count(p) from RidePerticipant p where p.ride.id = ?1 and p.role = 'passenger'")
    Long countPassengersByRideId(Integer rideId);
}
