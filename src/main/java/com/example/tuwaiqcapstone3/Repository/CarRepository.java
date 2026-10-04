package com.example.tuwaiqcapstone3.Repository;

import com.example.tuwaiqcapstone3.Model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    Car findCarById(Integer id);

    Car findCarByPlateNumber(String plateNumber);
}