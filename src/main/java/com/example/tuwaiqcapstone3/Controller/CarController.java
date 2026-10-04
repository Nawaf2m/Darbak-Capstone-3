package com.example.tuwaiqcapstone3.Controller;

import com.example.tuwaiqcapstone3.API.ApiResponse;
import com.example.tuwaiqcapstone3.Model.Car;
import com.example.tuwaiqcapstone3.Service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCars() {
        return ResponseEntity.status(200).body(carService.getAllCars());
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addCar(@PathVariable Integer userId, @RequestBody @Valid Car car) {
        carService.addCar(userId, car);
        return ResponseEntity.status(200).body(new ApiResponse("car added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Integer id, @RequestBody @Valid Car car) {
        carService.updateCar(id, car);
        return ResponseEntity.status(200).body(new ApiResponse("car updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.status(200).body(new ApiResponse("car deleted successfully"));
    }
}