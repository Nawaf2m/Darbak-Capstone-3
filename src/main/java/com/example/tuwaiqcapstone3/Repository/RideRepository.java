package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Integer> {

    Ride findRideById(Integer id);

    Integer countByDriverId(Integer driverId);

    Integer countByDriverIdAndStatus(Integer driverId, String status);

    @Query("select distinct r from Ride r left join r.ridePerticipants p where (r.driver.id = ?1 or p.user.id = ?1) and r.departureDate >= ?2 and r.status in ('available', 'full') order by r.departureDate, r.departureTime")
    List<Ride> findUpcomingRidesByUserId(Integer userId, LocalDate today);

    @Query("select count(distinct r.match.id) from Ride r left join r.ridePerticipants p where r.driver.id = ?1 or p.user.id = ?1")
    Integer countMatchesByUserId(Integer userId);

    @Query("select count(distinct r.match.id) from Ride r left join r.ridePerticipants p where (r.driver.id = ?1 or p.user.id = ?1) and r.match.startTime > ?2")
    Integer countUpcomingMatchesByUserId(Integer userId, LocalDateTime now);

    @Query("select distinct r.match.id from Ride r left join r.ridePerticipants p where " +
            "r.status in ('available', 'full') and " +
            "(r.driver.id = ?1 or (p.user.id = ?1 and p.role = 'passenger') or " +
            "exists (select rr.id from RideRequest rr where rr.ride = r " +
            "and rr.passenger.id = ?1 and rr.status = 'accepted'))")
    List<Integer> findArrangedMatchIdsByUserId(Integer userId);

    @Query("select r from Ride r where r.match.id = ?1 and r.departureDate = ?2 and r.status = 'available' and r.availableSeats >= ?3")
    List<Ride> findSuitableRides(Integer matchId, LocalDate date, Integer seats);

    @Query("select r from Ride r where r.status = 'available' and r.availableSeats > 0 and (r.departureDate > ?1 or (r.departureDate = ?1 and r.departureTime > ?2)) order by r.departureDate, r.departureTime")
    List<Ride> findAvailableRides(LocalDate date, LocalTime time);

    @Query("select r from Ride r where r.match.id = ?1 order by r.departureDate, r.departureTime")
    List<Ride> findRidesByMatchId(Integer matchId);

    @Query("select r from Ride r where r.driver.id = ?1 order by r.departureDate, r.departureTime")
    List<Ride> findRidesByDriverId(Integer driverId);

    @Query("select r from Ride r where r.car.id = ?1 and r.status in ('available', 'full')")
    List<Ride> findActiveRidesByCarId(Integer carId);

    @Query("select r from Ride r where r.driver.id = ?1 and r.status in ('available', 'full')")
    List<Ride> findActiveRidesByDriverId(Integer driverId);
}