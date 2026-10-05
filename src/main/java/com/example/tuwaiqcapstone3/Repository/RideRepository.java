package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride,Integer> {
    Ride findRideById(Integer id);
    Integer countByDriverId(Integer driverId);

    Integer countByDriverIdAndStatus(Integer driverId, String status);

    @Query("""
    SELECT DISTINCT r
    FROM Ride r
    LEFT JOIN r.ridePerticipants p
    WHERE (r.driver.id = :userId OR p.user.id = :userId)
    AND r.departureDate >= :today
    AND r.status IN ('available', 'full')
    ORDER BY r.departureDate, r.departureTime
    """)
    List<Ride> findUpcomingRidesByUserId(Integer userId,LocalDate today);


    @Query("""
    SELECT COUNT(DISTINCT r.match.id)
    FROM Ride r
    LEFT JOIN r.ridePerticipants p
    WHERE r.driver.id = :userId
       OR p.user.id = :userId
    """)
    Integer countMatchesByUserId(Integer userId);


    @Query("""
    SELECT COUNT(DISTINCT r.match.id)
    FROM Ride r
    LEFT JOIN r.ridePerticipants p
    WHERE (r.driver.id = :userId OR p.user.id = :userId)
    AND r.match.startTime > :now
    """)
    Integer countUpcomingMatchesByUserId(Integer userId,LocalDateTime now);
    //long if error
}
