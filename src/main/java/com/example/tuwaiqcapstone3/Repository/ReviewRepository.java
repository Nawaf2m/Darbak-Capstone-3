package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {

    Review findReviewById(Integer id);

    boolean existsByRideIdAndReviewerId(Integer rideId, Integer reviewerId);

}
