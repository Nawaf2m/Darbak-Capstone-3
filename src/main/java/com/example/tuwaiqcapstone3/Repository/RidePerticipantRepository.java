package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RidePerticipantRepository extends JpaRepository<RidePerticipant,Integer> {
    RidePerticipant findRidePerticipantById(Integer id);
}
