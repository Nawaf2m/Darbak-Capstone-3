package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest,Integer> {
    RideRequest findRideRequestById(Integer id);
}
