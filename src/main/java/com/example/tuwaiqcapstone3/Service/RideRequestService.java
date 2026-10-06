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

    public List<RideRequest> getRideRequests(){
        List<RideRequest> rideRequests = rideRequestRepository.findAll();
        if(rideRequests.isEmpty()){
            throw new ApiException("there is no requests in the system");
        }
        return rideRequests;
    }

    public void addRideRequest(Integer user_id,Integer ride_id,RideRequest rideRequest){

        Ride ride = rideRepository.findRideById(ride_id);

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(user_id);

        if(user==null){
            throw new ApiException("passenger id not found");
        }

        if(ride.getAvailableSeats()<=0){
            throw new ApiException("the ride is full ");
        }

        rideRequest.setRide(ride);
        rideRequest.setPassenger(user);
        rideRequest.setStatus("pending");
        rideRequestRepository.save(rideRequest);
        whatsAppService.notifyDriverNewRequest(ride, user);
    }

    public void updateRideRequest(Integer id, RideRequest rideRequest){
        RideRequest oldRideRequest = rideRequestRepository.findRideRequestById(id);

        if(oldRideRequest==null){
            throw new ApiException("ride request id not found");
        }

        Ride ride = rideRepository.findRideById(rideRequest.getRide().getId());

        if(ride==null){
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(rideRequest.getPassenger().getId());

        if(user==null){
            throw new ApiException("passenger id not found");
        }

        oldRideRequest.setStatus(rideRequest.getStatus());

        rideRequestRepository.save(oldRideRequest);
    }

    public void deleteRideRequest(Integer id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(id);

        if(rideRequest==null){
            throw new ApiException("ride request id not found");
        }

        rideRequestRepository.delete(rideRequest);
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


    public void AcceptRequest(Integer request_id,Integer driver_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!rideRequest.getStatus().equalsIgnoreCase("pending")){
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

        if(ride.getAvailableSeats()<=0){
            throw new ApiException("the ride is full ");
        }


        rideRequest.setStatus("accepted");
        rideRequestRepository.save(rideRequest);

        RidePerticipant ridePerticipant = new RidePerticipant();

        ridePerticipant.setUser(rideRequest.getPassenger());
        ridePerticipant.setRide(rideRequest.getRide());
        ridePerticipant.setRole("passenger");
        ridePerticipantRepository.save(ridePerticipant);

        ride.getRidePerticipants().add(ridePerticipant);
        ride.setAvailableSeats(ride.getAvailableSeats()-1);
        rideRepository.save(rideRequest.getRide());
        whatsAppService.notifyPassengerRequestAccepted(ride, rideRequest.getPassenger());
    }


    public void RejectRequest(Integer request_id,Integer driver_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!rideRequest.getStatus().equalsIgnoreCase("pending")){
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

        rideRequest.setStatus("rejected");
        rideRequestRepository.save(rideRequest);
        whatsAppService.notifyPassengerRequestRejected(ride, rideRequest.getPassenger());
    }

    public void CancelRequest(Integer request_id,Integer passenger_id){
        RideRequest rideRequest = rideRequestRepository.findRideRequestById(request_id);

        if(rideRequest==null){
            throw new ApiException("request not found");
        }

        if(!rideRequest.getStatus().equalsIgnoreCase("pending")){
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