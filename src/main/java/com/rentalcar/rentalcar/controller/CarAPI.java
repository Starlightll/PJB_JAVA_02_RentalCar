package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.mappers.CarMapper;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.CarOwnerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
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
    @Autowired
    private EmailService emailService;


    @GetMapping("/api/car/check-license-plate")
    public ResponseEntity<?> checkLicensePlate(@RequestParam String licensePlate, HttpSession session) {
        try {
            if (licensePlate == null || licensePlate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "License plate cannot be empty"));
            }

            User user = (User) session.getAttribute("user");
            Set<String> licensePlatesNotOwnedByUser = carOwnerService.getAllLicensePlatesNotOwnedByUser(user.getId());
            if (licensePlatesNotOwnedByUser.contains(licensePlate)) {
                return ResponseEntity.ok(Map.of(
                        "licensePlateOwnedByOther", true
                ));
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

    @GetMapping("/approve-car")
    public ResponseEntity<?> updateCarStatus(@RequestParam Integer carId) {
        try {
            Car car = carRepository.findById(carId).orElse(null);

            // Check if car exists and status is 'Verifying'
            if (car == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Car not found"));
            }
            if (car.getCarStatus().getStatusId() != 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Car status must be 'Verifying'"));
            }

            // Update car status
            carOwnerService.setCarStatus(carId, 1);

            // Send email
            Map<String, Object> variables = getStringObjectMap();
            emailService.sendEmailApprovedCar(car.getUser().getEmail(), "Car Approve", variables);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating car status"));
        }
    }

    private static Map<String, Object> getStringObjectMap() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("title1", "Bravo!");
        variables.put("title2", "Your Car is Approved and Ready to Rent.");
        variables.put("body", "The first mate and his Skipper too will do their very best to make the others comfortable in their tropic island nest. Michael Knight a young loner on a crusade to champion the cause of the innocent. The helpless. The powerless in a world of criminals who operate above the law. Here he comes Here comes Speed Racer. He's a demon on wheels..");
        return variables;
    }

}
