package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Model.User;
import com.example.tuwaiqcapstone3.Repository.CarRepository;
import com.example.tuwaiqcapstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

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
}