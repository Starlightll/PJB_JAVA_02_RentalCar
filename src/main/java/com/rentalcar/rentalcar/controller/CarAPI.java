package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mappers.CarMapper;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.CarOwnerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("carAPI")
public class CarAPI {

    @Autowired
    CarRepository carRepository;
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private CarOwnerService carOwnerService;


    @GetMapping("/api/car/check-license-plate")
    public ResponseEntity<?> checkLicensePlate(@RequestParam String licensePlate, HttpSession session) {
        try {
            if (licensePlate == null || licensePlate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "License plate cannot be empty"));
            }

            Long userOwnLicence = carRepository.findFirstUserByLicensePlate(licensePlate);
            if(userOwnLicence != null) {
                User user = (User) session.getAttribute("user");
                if(!Objects.equals(userOwnLicence, user.getId())) {
                    return ResponseEntity.ok(Map.of(
                            "licensePlateOwnedByOther", true
                    ));
                }
            }
            return ResponseEntity.ok(Map.of(
                    "licensePlateOwnedByOther", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking license plate"));
        }
    }

    @GetMapping("/get-cars-by-status")
    public ResponseEntity<?> getCarsByStatus(@RequestParam Integer statusId) {
        try {
            List<Car> cars = carRepository.findAllByCarStatus(statusId);
            return ResponseEntity.ok(cars.stream().map(carMapper::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting cars by status"));
        }
    }

    @GetMapping("/update-car-status/{carId}")
    public ResponseEntity<?> updateCarStatus(@RequestParam Integer statusId, @PathVariable("carId") Integer carId) {
        try {
            Car car = carRepository.findById(carId).orElse(null);
            if (car == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Car not found"));
            }
            carOwnerService.setCarStatus(carId, statusId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating car status"));
        }
    }

}
