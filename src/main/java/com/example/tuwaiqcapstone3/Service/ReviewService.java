package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.DTO.ReviewDTO;
import com.example.tuwaiqcapstone3.Model.Review;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.ReviewRepository;
import com.example.tuwaiqcapstone3.Repository.RidePerticipantRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RidePerticipantRepository ridePerticipantRepository;


    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    public void addReview(ReviewDTO reviewDTO){
        Ride ride = rideRepository.findRideById(reviewDTO.getRideId());
        if (ride == null) {
            throw new ApiException("Ride not found");
        }

        User passenger = userRepository.findUserById(reviewDTO.getPassengerId());
        if (passenger == null) {
            throw new ApiException("Passenger not found");
        }

        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new ApiException("The driver cannot review his own ride");
        }

        if (!"completed".equals(ride.getStatus())) {
            throw new ApiException("You can review a ride only after it is completed");
        }
        if (!ridePerticipantRepository.existsByRideIdAndUserId(ride.getId(), passenger.getId())) {
            throw new ApiException("You did not join this ride");
        }

        if (reviewRepository.existsByRideIdAndReviewerId(ride.getId(), passenger.getId())) {
            throw new ApiException("You already reviewed this ride");
        }

        Review review = new Review(null, reviewDTO.getRating(), reviewDTO.getComment(), null, passenger, ride.getDriver(), ride);
        reviewRepository.save(review);

    }

    public void updateReview(Integer id, Review updatedReview) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());
        reviewRepository.save(review);
    }

    public void deleteReview(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        reviewRepository.delete(review);
    }
}
