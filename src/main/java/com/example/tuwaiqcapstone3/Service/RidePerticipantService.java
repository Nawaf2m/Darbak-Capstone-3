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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RidePerticipantService {

    private final RidePerticipantRepository ridePerticipantRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final RideRequestRepository rideRequestRepository;
    private final WhatsAppService whatsAppService;

    public List<RidePerticipant> getRidePerticipants() {
        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findAll();

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("there is no ride participants");
        }

        return ridePerticipants;
    }

    // Adds a participant and reserves a seat only for passengers.
    public void addRidePerticipant(Integer ride_id, Integer user_id, RidePerticipant ridePerticipant) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride id not found");
        }

        User user = userRepository.findUserById(user_id);

        if (user == null) {
            throw new ApiException("user id not found");
        }

        if ("driver".equals(ridePerticipant.getRole())) {
            validateRideBeforeDeparture(ride);
            if (!ride.getDriver().getId().equals(user_id)) {
                throw new ApiException("driver participant must be the assigned ride driver");
            }
            if (ridePerticipantRepository.existsByRideIdAndUserId(ride_id, user_id)) {
                throw new ApiException("driver already joined this ride");
            }
            RidePerticipant driverParticipant = new RidePerticipant();
            driverParticipant.setRide(ride);
            driverParticipant.setUser(user);
            driverParticipant.setRole("driver");
            ridePerticipantRepository.save(driverParticipant);
            return;
        }

        if (!"passenger".equals(ridePerticipant.getRole())) {
            throw new ApiException("role must be passenger or driver");
        }

        addPassengerToRide(ride, user, null);
    }

    // Preserves the participant's role, membership, and automatically generated join date.
    public void updateRidePerticipant(Integer id, RidePerticipant ridePerticipant) {
        RidePerticipant oldRidePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if (oldRidePerticipant == null) {
            throw new ApiException("ride participant id not found");
        }

        if ((!"passenger".equals(ridePerticipant.getRole()) && !"driver".equals(ridePerticipant.getRole()))
                || !ridePerticipant.getRole().equals(oldRidePerticipant.getRole())) {
            throw new ApiException("participant role cannot be changed");
        }
    }

    // Removes a participant before departure and restores the seat only for a passenger.
    public void deleteRidePerticipant(Integer id) {
        RidePerticipant ridePerticipant = ridePerticipantRepository.findRidePerticipantById(id);

        if (ridePerticipant == null) {
            throw new ApiException("ride participant id not found");
        }

        Ride ride = ridePerticipant.getRide();
        validateRideBeforeDeparture(ride);

        if ("driver".equals(ridePerticipant.getRole())) {
            ridePerticipantRepository.delete(ridePerticipant);
            if (ride.getRidePerticipants() != null) {
                ride.getRidePerticipants().removeIf(participant -> participant.getId().equals(id));
            }
            return;
        }

        if (!"passenger".equals(ridePerticipant.getRole())
                || ride.getDriver().getId().equals(ridePerticipant.getUser().getId())) {
            throw new ApiException("only passengers can have a reserved seat restored");
        }

        ridePerticipantRepository.delete(ridePerticipant);
        if (ride.getRidePerticipants() != null) {
            ride.getRidePerticipants().removeIf(participant -> participant.getId().equals(id));
        }

        List<RideRequest> requests = rideRequestRepository.findRideRequestByRideAndPassenger(ride, ridePerticipant.getUser());
        for (RideRequest request : requests) {
            if ("accepted".equals(request.getStatus()) || "pending".equals(request.getStatus())) {
                rideRequestRepository.delete(request);
            }
        }

        ride.setAvailableSeats(ride.getAvailableSeats() + 1);
        ride.setStatus("available");
        rideRepository.save(ride);
    }

    // Checks that a ride is active and has not departed.
    public void validateRideBeforeDeparture(Ride ride) {
        if (!"available".equals(ride.getStatus()) && !"full".equals(ride.getStatus())) {
            throw new ApiException("ride is not available for booking changes");
        }

        LocalDateTime departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());
        if (!departure.isAfter(LocalDateTime.now())) {
            throw new ApiException("booking cannot be changed after departure");
        }

        if (ride.getMatch() != null && ("cancelled".equals(ride.getMatch().getStatus())
                || "completed".equals(ride.getMatch().getStatus()))) {
            throw new ApiException("match is not available for booking changes");
        }
    }

    // Checks availability before a passenger requests or takes a seat.
    public void validateRideForBooking(Ride ride) {
        validateRideBeforeDeparture(ride);
        if (!"available".equals(ride.getStatus()) || ride.getAvailableSeats() <= 0) {
            throw new ApiException("the ride is full");
        }
    }

    // Uses the same booking rules for request acceptance and direct passenger creation.
    public void addPassengerToRide(Ride ride, User user, RideRequest acceptedRequest) {
        validateRideForBooking(ride);

        if (ride.getDriver().getId().equals(user.getId())) {
            throw new ApiException("driver cannot join their own ride");
        }

        if (ridePerticipantRepository.existsByRideIdAndUserId(ride.getId(), user.getId())) {
            throw new ApiException("passenger already joined this ride");
        }

        Long passengerCount = ridePerticipantRepository.countPassengersByRideId(ride.getId());
        if (ride.getAvailableSeats() + passengerCount > ride.getCar().getSeatsCount() - 1) {
            throw new ApiException("available seats and passengers exceed car capacity");
        }

        List<RideRequest> requests = rideRequestRepository.findRideRequestByRideAndPassenger(ride, user);
        for (RideRequest request : requests) {
            if ("accepted".equals(request.getStatus())) {
                throw new ApiException("passenger already has an accepted request");
            }
            if (acceptedRequest == null && "pending".equals(request.getStatus())) {
                acceptedRequest = request;
            }
        }

        if (acceptedRequest == null) {
            acceptedRequest = new RideRequest();
            acceptedRequest.setRide(ride);
            acceptedRequest.setPassenger(user);
        }

        RidePerticipant participant = new RidePerticipant();
        participant.setRide(ride);
        participant.setUser(user);
        participant.setRole("passenger");
        ridePerticipantRepository.save(participant);

        if (ride.getRidePerticipants() == null) {
            ride.setRidePerticipants(new HashSet<>());
        }
        ride.getRidePerticipants().add(participant);
        ride.setAvailableSeats(ride.getAvailableSeats() - 1);
        ride.setStatus(ride.getAvailableSeats() == 0 ? "full" : "available");
        rideRepository.save(ride);

        acceptedRequest.setStatus("accepted");
        rideRequestRepository.save(acceptedRequest);
        for (RideRequest request : requests) {
            if (!request.getId().equals(acceptedRequest.getId()) && "pending".equals(request.getStatus())) {
                request.setStatus("rejected");
                rideRequestRepository.save(request);
            }
        }
        whatsAppService.notifyPassengerRequestAccepted(ride, user);
    }

    public RidePerticipant getSpecificRidePerticipant(Integer user_id, Integer ride_id) {
        User user = userRepository.findUserById(user_id);

        if (user == null) {
            throw new ApiException("user not found");
        }

        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        RidePerticipant ridePerticipant = ridePerticipantRepository.findRidePerticipantByUserAndRide(user, ride);

        if (ridePerticipant == null) {
            throw new ApiException("ride participant not found");
        }

        return ridePerticipant;
    }

    public List<RidePerticipant> GetAllParticipantsInARide(Integer ride_id) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findRidePerticipantByRide(ride);

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("ride dont have participant");
        }

        return ridePerticipants;
    }

    public List<RidePerticipant> GetParticipantsByRoleInARide(Integer ride_id, String role) {
        Ride ride = rideRepository.findRideById(ride_id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        List<RidePerticipant> ridePerticipants = ridePerticipantRepository.findRidePerticipantByRideAndRole(ride, role);

        if (ridePerticipants.isEmpty()) {
            throw new ApiException("there is no participant with this role");
        }

        return ridePerticipants;
    }

    public Long getPassengerCount(Integer rideId) {
        Ride ride = rideRepository.findRideById(rideId);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        return ridePerticipantRepository.countPassengersByRideId(rideId);
    }
}
