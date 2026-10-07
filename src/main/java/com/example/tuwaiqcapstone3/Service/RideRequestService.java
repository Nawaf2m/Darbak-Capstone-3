package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Model.RideRequest;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.RidePerticipantRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.RideRequestRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RidePerticipantRepository ridePerticipantRepository;
    private final WhatsAppService whatsAppService;
    private final RidePerticipantService ridePerticipantService;

    public List<RideRequest> getRideRequests(){
        List<RideRequest> rideRequests = rideRequestRepository.findAll();
        if(rideRequests.isEmpty()){
            throw new ApiException("there is no requests in the system");
        }
        return rideRequests;
    }

    // Creates a pending request only when the passenger can book the ride.
    public void addRideRequest(Integer user_id,Integer ride_id,RideRequest rideRequest){

        Ride ride = rideRepository.findRideById(ride_id);

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(user_id);

        if(user==null){
            throw new ApiException("passenger id not found");
        }

        ridePerticipantService.validateRideForBooking(ride);

        if (ride.getDriver().getId().equals(user_id)) {
            throw new ApiException("driver cannot join their own ride");
        }

        if (ridePerticipantRepository.existsByRideIdAndUserId(ride_id, user_id)) {
            throw new ApiException("passenger already joined this ride");
        }

        List<RideRequest> requests = rideRequestRepository.findRideRequestByRideAndPassenger(ride, user);
        for (RideRequest request : requests) {
            if ("pending".equals(request.getStatus()) || "accepted".equals(request.getStatus())) {
                throw new ApiException("passenger already has a pending or accepted request");
            }
        }

        rideRequest.setId(null);
        rideRequest.setRide(ride);
        rideRequest.setPassenger(user);
        rideRequest.setStatus("pending");
        rideRequestRepository.save(rideRequest);
        whatsAppService.notifyDriverNewRequest(ride, user);
    }

    // Routes status changes through the same checks used by acceptance and rejection.
    public void updateRideRequest(Integer id, RideRequest rideRequest){
        RideRequest oldRideRequest = rideRequestRepository.findRideRequestById(id);

        if(oldRideRequest==null){
            throw new ApiException("ride request id not found");
        }

        if (!"pending".equals(oldRideRequest.getStatus())) {
            throw new ApiException("only pending requests can be updated");
        }

        Integer driverId = oldRideRequest.getRide().getDriver().getId();
        if ("accepted".equals(rideRequest.getStatus())) {
            AcceptRequest(id, driverId);
        } else if ("rejected".equals(rideRequest.getStatus())) {
            RejectRequest(id, driverId);
        } else if (!"pending".equals(rideRequest.getStatus())) {
            throw new ApiException("status must be pending, accepted, or rejected");
        }
    }

    // Deletes a request and restores its seat if it was an accepted booking.
    public void deleteRideRequest(Integer id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(id);

        if(rideRequest==null){
            throw new ApiException("ride request id not found");
        }

        if ("accepted".equals(rideRequest.getStatus())) {
            RidePerticipant participant = ridePerticipantRepository.findRidePerticipantByUserAndRide(
                    rideRequest.getPassenger(), rideRequest.getRide());
            if (participant == null) {
                throw new ApiException("accepted request has no participant");
            }
            ridePerticipantService.deleteRidePerticipant(participant.getId());
        } else {
            rideRequestRepository.delete(rideRequest);
        }
    }

    public List<RideRequest> ViewRequestsForARide(Integer ride_id){
        Ride ride = rideRepository.findRideById(ride_id);

        if(ride==null){
            throw new ApiException("ride id not found");
        }
        List<RideRequest> rideRequests = rideRequestRepository.findRideRequestByRide(ride);

        if(rideRequests.isEmpty()){
            throw new ApiException("there is no requests for this ride");
        }
        return rideRequests;
    }

    public List<RideRequest> ViewRequestsForAUser(Integer user_id){
        User user = userRepository.findUserById(user_id);

        if(user==null){
            throw new ApiException("user id not found");
        }
        List<RideRequest> rideRequests = rideRequestRepository.findRideRequestByPassenger(user);

        if(rideRequests.isEmpty()){
            throw new ApiException("there is no requests for this user");
        }
        return rideRequests;
    }


    // Accepts a pending request and reserves exactly one passenger seat.
    public void AcceptRequest(Integer request_id,Integer driver_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!"pending".equals(rideRequest.getStatus())){
            throw new ApiException("the request is not pending");
        }

        User user = userRepository.findUserById(driver_id);
        if(user==null){
            throw new ApiException("driver not found");
        }

        Ride ride = rideRepository.findRideById(rideRequest.getRide().getId());
        if(ride == null){
            throw new ApiException("ride not found ");
        }

        if(!ride.getDriver().getId().equals(driver_id)){
            throw new ApiException("driver dont own the ride");
        }

        ridePerticipantService.addPassengerToRide(ride, rideRequest.getPassenger(), rideRequest);
    }


    // Rejects a pending request without reserving a seat.
    public void RejectRequest(Integer request_id,Integer driver_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!"pending".equals(rideRequest.getStatus())){
            throw new ApiException("the request is not pending");
        }

        User user = userRepository.findUserById(driver_id);
        if(user==null){
            throw new ApiException("driver not found");
        }

        Ride ride = rideRepository.findRideById(rideRequest.getRide().getId());
        if(ride == null){
            throw new ApiException("ride not found ");
        }

        if(!ride.getDriver().getId().equals(driver_id)){
            throw new ApiException("driver dont own the ride");
        }

        ridePerticipantService.validateRideBeforeDeparture(ride);
        rideRequest.setStatus("rejected");
        rideRequestRepository.save(rideRequest);
        whatsAppService.notifyPassengerRequestRejected(ride, rideRequest.getPassenger());
    }

    public void CancelRequest(Integer request_id,Integer passenger_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!"pending".equals(rideRequest.getStatus())){
            throw new ApiException("the request is not pending");
        }

        User user = userRepository.findUserById(passenger_id);
        if(user==null){
            throw new ApiException("passenger not found");
        }

        if(!rideRequest.getPassenger().getId().equals(passenger_id)){
            throw new ApiException("passenger dont own the request");
        }

        rideRequestRepository.delete(rideRequest);
    }

    public List<RideRequest> getUserPendingRequest(Integer user_id){
        User user = userRepository.findUserById(user_id);
        if(user==null){
            throw new ApiException("user not found");
        }

        List<RideRequest> rideRequests = rideRequestRepository.findRideRequestByPassengerAndStatus(user,"pending");

        if(rideRequests.isEmpty()){
            throw new ApiException("you dont have requests");
        }

        return rideRequests;

    }
}
