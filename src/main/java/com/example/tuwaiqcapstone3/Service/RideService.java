package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.DTO.RecommendedRideDTO;
import com.example.tuwaiqcapstone3.DTO.RideMapDetailsDTO;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Model.Match;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final CarRepository carRepository;
    private final CarService carService;
    private final RidePerticipantRepository ridePerticipantRepository;
    private final WhatsAppService whatsAppService;

    public List<Ride> getRides() {
        List<Ride> rides = rideRepository.findAll();

        if (rides.isEmpty()) {
            throw new ApiException("there is no rides in the system");
        }

        return rides;
    }

    // Returns the meeting point, stadium, and directions links for a ride.
    public RideMapDetailsDTO getRideMapDetails(Integer rideId) {
        Ride ride = rideRepository.findRideById(rideId);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        Match match = ride.getMatch();
        Stadium stadium = match == null ? null : match.getStadium();

        if (stadium == null || stadium.getLatitude() == null || stadium.getLongitude() == null
                || ride.getMeetingLatitude() == null || ride.getMeetingLongitude() == null) {
            throw new ApiException("ride or stadium location is not available");
        }

        String meeting = ride.getMeetingLatitude() + "%2C" + ride.getMeetingLongitude();
        String destination = stadium.getLatitude().toPlainString()
                + "%2C" + stadium.getLongitude().toPlainString();
        String directions = "https://www.google.com/maps/dir/?api=1&travelmode=driving";

        return new RideMapDetailsDTO(
                ride.getId(), match.getId(), ride.getMeetingPoint(),
                ride.getMeetingLatitude(), ride.getMeetingLongitude(),
                stadium.getId(), stadium.getName(),
                stadium.getLatitude(), stadium.getLongitude(),
                directions + "&destination=" + meeting,
                directions + "&destination=" + destination,
                directions + "&origin=" + meeting + "&destination=" + destination
        );
    }

    public void addRide(Integer driverId, Integer matchId, Integer carId, Ride ride) {
        User driver = userRepository.findUserById(driverId);

        if (driver == null) {
            throw new ApiException("driver not found");
        }

        Match match = matchRepository.findMatchById(matchId);

        if (match == null) {
            throw new ApiException("match not found");
        }

        Car car = carRepository.findCarById(carId);

        if (car == null) {
            throw new ApiException("car not found");
        }

        if (car.getDriver() == null || !car.getDriver().getId().equals(driverId)) {
            throw new ApiException("car does not belong to this driver");
        }

        if (ride.getAvailableSeats() == null || ride.getAvailableSeats() < 1) {
            throw new ApiException("ride must have at least one available seat");
        }

        if (ride.getAvailableSeats() > car.getSeatsCount() - 1) {
            throw new ApiException("available seats exceed car passenger capacity");
        }

        LocalDateTime departureDateTime = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());

        if (!departureDateTime.isAfter(LocalDateTime.now())) {
            throw new ApiException("departure must be in the future");
        }

        if (match.getStartTime() == null) {
            throw new ApiException("match start time is not available");
        }

        if (!departureDateTime.isBefore(match.getStartTime())) {
            throw new ApiException("departure must be before match start time");
        }

        if ("cancelled".equals(match.getStatus()) || "completed".equals(match.getStatus())) {
            throw new ApiException("cannot create a ride for this match");
        }

        if (carService.checkAvailabilityConflict(carId, departureDateTime, ride.getExpectedArrivalTime())) {
            throw new ApiException("car has another ride during this time");
        }

        if (!ride.getExpectedArrivalTime().isBefore(match.getStartTime())) {
            throw new ApiException("expected arrival must be before match start time");
        }

        if (checkDriverAvailabilityConflict(driverId, departureDateTime, ride.getExpectedArrivalTime(), null)) {
            throw new ApiException("driver has another ride during this time");
        }

        ride.setId(null);
        ride.setDriver(driver);
        ride.setMatch(match);
        ride.setCar(car);
        ride.setStatus("available");

        rideRepository.save(ride);
    }

    public void updateRide(Integer id, Integer driverId, Ride ride) {
        Ride oldRide = rideRepository.findRideById(id);

        if (oldRide == null) {
            throw new ApiException("ride id not found");
        }

        if (oldRide.getDriver() == null || !oldRide.getDriver().getId().equals(driverId)) {
            throw new ApiException("ride does not belong to this driver");
        }

        if (!"available".equals(oldRide.getStatus()) && !"full".equals(oldRide.getStatus())) {
            throw new ApiException("ride cannot be updated in its current status");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldDeparture = LocalDateTime.of(oldRide.getDepartureDate(), oldRide.getDepartureTime());

        if (!oldDeparture.isAfter(now)) {
            throw new ApiException("ride cannot be updated after departure time");
        }

        LocalDateTime departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());

        if (!departure.isAfter(now)) {
            throw new ApiException("departure must be in the future");
        }

        Match match = oldRide.getMatch();

        if (match == null || match.getStartTime() == null) {
            throw new ApiException("match start time is not available");
        }

        if ("cancelled".equals(match.getStatus()) || "completed".equals(match.getStatus())) {
            throw new ApiException("cannot update a ride for this match");
        }

        if (oldRide.getCar() == null) {
            throw new ApiException("ride car not found");
        }

        if (carService.checkAvailabilityConflict(oldRide.getCar().getId(), departure, ride.getExpectedArrivalTime(), id)) {
            throw new ApiException("car has another ride during this time");
        }

        if (!ride.getExpectedArrivalTime().isBefore(match.getStartTime())) {
            throw new ApiException("expected arrival must be before match start time");
        }

        if (ride.getAvailableSeats() == null || ride.getAvailableSeats() < 0) {
            throw new ApiException("available seats can not be negative");
        }

        Long passengerCount = ridePerticipantRepository.countPassengersByRideId(id);

        if (ride.getAvailableSeats() + passengerCount > oldRide.getCar().getSeatsCount() - 1) {
            throw new ApiException("available seats and passengers exceed car capacity");
        }

        if (checkDriverAvailabilityConflict(oldRide.getDriver().getId(), departure, ride.getExpectedArrivalTime(), id)) {
            throw new ApiException("driver has another ride during this time");
        }

        oldRide.setDepartureDate(ride.getDepartureDate());
        oldRide.setDepartureTime(ride.getDepartureTime());
        oldRide.setExpectedArrivalTime(ride.getExpectedArrivalTime());
        oldRide.setMeetingPoint(ride.getMeetingPoint());
        oldRide.setMeetingLatitude(ride.getMeetingLatitude());
        oldRide.setMeetingLongitude(ride.getMeetingLongitude());
        oldRide.setDestination(ride.getDestination());
        oldRide.setAvailableSeats(ride.getAvailableSeats());
        oldRide.setNotes(ride.getNotes());

        if (oldRide.getAvailableSeats() == 0) {
            oldRide.setStatus("full");
        } else {
            oldRide.setStatus("available");
        }

        rideRepository.save(oldRide);
    }

    public void deleteRide(Integer id, Integer driverId) {
        Ride ride = rideRepository.findRideById(id);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
            throw new ApiException("ride does not belong to this driver");
        }

        rideRepository.delete(ride);
    }

    public List<Ride> getSuitableRides(Integer matchId, LocalDate date, Integer seats) {
        Match match = matchRepository.findMatchById(matchId);

        if (match == null) {
            throw new ApiException("match not found");
        }

        if (seats == null || seats < 1) {
            throw new ApiException("requested seats must be at least 1");
        }

        LocalDateTime now = LocalDateTime.now();

        if (date == null || date.isBefore(now.toLocalDate())) {
            throw new ApiException("departure date must be today or in the future");
        }

        List<Ride> rides = rideRepository.findSuitableRides(matchId, date, seats);
        List<Ride> suitableRides = new ArrayList<>();

        for (Ride ride : rides) {
            LocalDateTime departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());

            if (departure.isAfter(now)) {
                suitableRides.add(ride);
            }
        }

        return suitableRides;
    }

    public List<Ride> getAvailableRides() {
        LocalDateTime now = LocalDateTime.now();
        List<Ride> rides = rideRepository.findAvailableRides(now.toLocalDate(), now.toLocalTime());

        if (rides.isEmpty()) {
            throw new ApiException("no available rides found");
        }

        return rides;
    }

    /*
        Recommend rides for the user saved matches,
        based on the following rules:
            - shows rides that still have seats,
            - leave in the future,
            - and are expected to arrive before the match begins.
     */
    public List<RecommendedRideDTO> getRecommendedRidesForUserMatches(Integer userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("user not found");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Match> matches = matchRepository.findDistinctMatchesByUsers_Id(userId);
        Set<Integer> matchIds = new HashSet<>();

        for (Match match : matches) {
            if ("scheduled".equals(match.getStatus()) && match.getStartTime().isAfter(now)) {
                matchIds.add(match.getId());
            }
        }

        List<Ride> rides = rideRepository.findAvailableRides(now.toLocalDate(), now.toLocalTime());
        List<RecommendedRideDTO> recommendations = new ArrayList<>();

        for (Ride ride : rides) {
            Match match = ride.getMatch();

            if (match == null || !matchIds.contains(match.getId())) {
                continue;
            }

            if (ride.getDriver().getId().equals(userId)
                    || !ride.getExpectedArrivalTime().isBefore(match.getStartTime())
                    || ridePerticipantRepository.existsByRideIdAndUserId(ride.getId(), userId)) {
                continue;
            }

            recommendations.add(new RecommendedRideDTO(
                    ride.getId(), match.getId(), match.getHomeTeam(), match.getAwayTeam(),
                    match.getStartTime(), ride.getDepartureDate(), ride.getDepartureTime(),
                    ride.getExpectedArrivalTime(), ride.getMeetingPoint(), ride.getMeetingLatitude(),
                    ride.getMeetingLongitude(), ride.getAvailableSeats()));
        }

        recommendations.sort(Comparator.comparing(RecommendedRideDTO::getMatchStartTime)
                .thenComparing(RecommendedRideDTO::getDepartureDate)
                .thenComparing(RecommendedRideDTO::getDepartureTime));

        return recommendations;
    }

    public List<Ride> getRidesByMatchId(Integer matchId) {
        Match match = matchRepository.findMatchById(matchId);

        if (match == null) {
            throw new ApiException("match not found");
        }

        List<Ride> rides = rideRepository.findRidesByMatchId(matchId);

        if (rides.isEmpty()) {
            throw new ApiException("no rides found for this match");
        }

        return rides;
    }

    public List<Ride> getRidesByDriverId(Integer driverId) {
        User driver = userRepository.findUserById(driverId);

        if (driver == null) {
            throw new ApiException("driver not found");
        }

        List<Ride> rides = rideRepository.findRidesByDriverId(driverId);

        if (rides.isEmpty()) {
            throw new ApiException("no rides found for this driver");
        }

        return rides;
    }

    public void completeRide(Integer rideId, Integer driverId) {
        Ride ride = rideRepository.findRideById(rideId);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
            throw new ApiException("ride does not belong to this driver");
        }

        if ("completed".equals(ride.getStatus())) {
            throw new ApiException("ride is already completed");
        }

        if (!"available".equals(ride.getStatus()) && !"full".equals(ride.getStatus())) {
            throw new ApiException("ride cannot be completed in its current status");
        }

        LocalDateTime departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());

        if (departure.isAfter(LocalDateTime.now())) {
            throw new ApiException("ride cannot be completed before departure");
        }

        ride.setStatus("completed");
        rideRepository.save(ride);
        whatsAppService.notifyRideStatusChanged(ride);
    }

    public void cancelRide(Integer rideId, Integer driverId) {
        Ride ride = rideRepository.findRideById(rideId);

        if (ride == null) {
            throw new ApiException("ride not found");
        }

        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driverId)) {
            throw new ApiException("ride does not belong to this driver");
        }

        if ("cancelled".equals(ride.getStatus())) {
            throw new ApiException("ride is already cancelled");
        }

        if (!"available".equals(ride.getStatus()) && !"full".equals(ride.getStatus())) {
            throw new ApiException("ride cannot be cancelled in its current status");
        }

        LocalDateTime departure = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());

        if (!departure.isAfter(LocalDateTime.now())) {
            throw new ApiException("ride cannot be cancelled after departure time");
        }

        ride.setStatus("cancelled");
        rideRepository.save(ride);
        whatsAppService.notifyRideStatusChanged(ride);
    }

    private boolean checkDriverAvailabilityConflict(Integer driverId, LocalDateTime departure, LocalDateTime expectedArrival, Integer excludedRideId) {
        if (departure == null || expectedArrival == null) {
            throw new ApiException("departure and expected arrival are required");
        }

        if (!expectedArrival.isAfter(departure)) {
            throw new ApiException("expected arrival must be after departure");
        }

        List<Ride> rides = rideRepository.findActiveRidesByDriverId(driverId);

        for (Ride ride : rides) {
            if (ride.getId().equals(excludedRideId)) {
                continue;
            }

            LocalDateTime existingDeparture = LocalDateTime.of(ride.getDepartureDate(), ride.getDepartureTime());
            LocalDateTime existingArrival = ride.getExpectedArrivalTime();

            if (existingArrival == null) {
                throw new ApiException("expected arrival is missing for ride " + ride.getId());
            }

            if (departure.isBefore(existingArrival) && expectedArrival.isAfter(existingDeparture)) {
                return true;
            }
        }

        return false;
    }
}
