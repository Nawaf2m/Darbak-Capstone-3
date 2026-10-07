package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.DTO.AiReviewCheckDTO;
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
    private final AiService aiService;


    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    // Allows completed-ride reviews between the driver and an enrolled passenger.
    public void addReview(ReviewDTO reviewDTO){
        Ride ride = rideRepository.findRideById(reviewDTO.getRideId());
        if (ride == null) {
            throw new ApiException("Ride not found");
        }

        User reviewer = userRepository.findUserById(reviewDTO.getReviewerId());
        if (reviewer == null) {
            throw new ApiException("Reviewer not found");
        }

        User reviewedUser = userRepository.findUserById(reviewDTO.getReviewedUserId());
        if (reviewedUser == null) {
            throw new ApiException("Reviewed user not found");
        }

        validateReviewParticipants(ride, reviewer, reviewedUser);

        if (reviewRepository.existsByRideIdAndReviewerIdAndReviewedUserId(ride.getId(), reviewer.getId(), reviewedUser.getId())) {
            throw new ApiException("You already reviewed this user for this ride");
        }

        validateComment(reviewDTO.getComment());

        Review review = new Review(null, reviewDTO.getRating(), reviewDTO.getComment(), null, reviewer, reviewedUser, ride);
        reviewRepository.save(review);

    }

    // Updates a review after checking its original participants and moderating the new comment.
    public void updateReview(Integer id, Review updatedReview) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review not found");
        }
        validateReviewParticipants(review.getRide(), review.getReviewer(), review.getReviewedUser());
        validateComment(updatedReview.getComment());
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

    // Checks that the review is between the ride driver and one of their passengers.
    private void validateReviewParticipants(Ride ride, User reviewer, User reviewedUser) {
        if (!"completed".equals(ride.getStatus())) {
            throw new ApiException("You can review a ride only after it is completed");
        }

        if (reviewer.getId().equals(reviewedUser.getId())) {
            throw new ApiException("You cannot review yourself");
        }

        boolean reviewerIsDriver = ride.getDriver().getId().equals(reviewer.getId());
        boolean reviewedUserIsDriver = ride.getDriver().getId().equals(reviewedUser.getId());
        if (reviewerIsDriver == reviewedUserIsDriver) {
            throw new ApiException("Reviews must be between the ride driver and a passenger");
        }

        Integer passengerId = reviewerIsDriver ? reviewedUser.getId() : reviewer.getId();
        if (!ridePerticipantRepository.existsByRideIdAndUserIdAndRole(ride.getId(), passengerId, "passenger")) {
            throw new ApiException("Passenger did not join this ride");
        }
    }

    // Moderates written comments while allowing a rating without a comment.
    private void validateComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return;
        }

        AiReviewCheckDTO response = aiService.checkInappropriateReview(comment);
        if (response == null || response.getInappropriate() == null) {
            throw new ApiException("AI review check is unavailable");
        }
        if (response.getInappropriate()) {
            throw new ApiException("Comment is inappropriate: " + response.getReason());
        }
    }
}
