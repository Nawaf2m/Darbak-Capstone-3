package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.CarRepository;
import com.example.tuwaiqcapstone3.Repository.RidePerticipantRepository;
import com.example.tuwaiqcapstone3.Repository.RideRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final RidePerticipantRepository ridePerticipantRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public void addCar(Integer userId, Car car) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("user not found");
        }

        Car checkPlate = carRepository.findCarByPlateNumber(car.getPlateNumber());

        if (checkPlate != null) {
            throw new ApiException("plate number already exists");
        }

        car.setDriver(user);

        carRepository.save(car);
    }

    public void updateCar(Integer id, Car car) {
        Car oldCar = carRepository.findCarById(id);

        if (oldCar == null) {
            throw new ApiException("car not found");
        }

        Car checkPlate = carRepository.findCarByPlateNumber(car.getPlateNumber());

        if (checkPlate != null && !checkPlate.getId().equals(id)) {
            throw new ApiException("plate number already exists");
        }

        if (car.getSeatsCount() == null || car.getSeatsCount() < 1) {
            throw new ApiException("car must have at least one seat");
        }

        List<Ride> rides = rideRepository.findActiveRidesByCarId(id);

        for (Ride ride : rides) {
            Long passengerCount = ridePerticipantRepository.countPassengersByRideId(ride.getId());

            if (ride.getAvailableSeats() + passengerCount + 1 > car.getSeatsCount()) {
                throw new ApiException("new car capacity is not enough for ride " + ride.getId());
            }
        }

        oldCar.setCarName(car.getCarName());
        oldCar.setPlateNumber(car.getPlateNumber());
        oldCar.setSeatsCount(car.getSeatsCount());
        oldCar.setColor(car.getColor());

        carRepository.save(oldCar);
    }

    public void deleteCar(Integer id) {

        Car car = carRepository.findCarById(id);

        if (car == null) {
            throw new ApiException("car not found");
        }

        carRepository.delete(car);
    }

    public boolean checkAvailabilityConflict(Integer carId, LocalDateTime departure, LocalDateTime expectedArrival) {
        return checkAvailabilityConflict(carId, departure, expectedArrival, null);
    }

    public boolean checkAvailabilityConflict(Integer carId, LocalDateTime departure, LocalDateTime expectedArrival, Integer excludedRideId) {
        Car car = carRepository.findCarById(carId);

        if (car == null) {
            throw new ApiException("car not found");
        }

        if (departure == null || expectedArrival == null) {
            throw new ApiException("departure and expected arrival are required");
        }

        if (!expectedArrival.isAfter(departure)) {
            throw new ApiException("expected arrival must be after departure");
        }

        List<Ride> rides = rideRepository.findActiveRidesByCarId(carId);

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