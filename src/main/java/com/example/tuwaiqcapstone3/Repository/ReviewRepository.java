package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {

    Review findReviewById(Integer id);

    boolean existsByRideIdAndReviewerIdAndReviewedUserId(Integer rideId, Integer reviewerId, Integer reviewedUserId);

    @Query("select avg(r.rating) from Review r where r.reviewedUser.id = :userId")
    Double findAverageRatingByUserId(Integer userId);

    @Query("select r from Review r where r.reviewedUser.id = ?1 order by r.createdAt desc")
    List<Review> findReviewsByUserId(Integer userId);
}
