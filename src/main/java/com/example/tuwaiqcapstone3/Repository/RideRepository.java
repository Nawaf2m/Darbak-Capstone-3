package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride,Integer> {
    Ride findRideById(Integer id);

}
