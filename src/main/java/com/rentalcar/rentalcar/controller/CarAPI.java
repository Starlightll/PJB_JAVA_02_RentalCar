package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
public class CarAPI {

    private final CarRepository carRepository;

    public CarAPI(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/api/car/check-license-plate")
    @ResponseBody
    public ResponseEntity<?> checkLicensePlate(@RequestParam String licensePlate) {
        try {
            // Check for null or empty license plate
            if (licensePlate == null || licensePlate.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "License plate cannot be empty"));
            }

            // Use a single database call instead of two
            String foundLicensePlate = carRepository.findFirstLicensePlateMatchNative(licensePlate);
            boolean exists = foundLicensePlate != null;

            return ResponseEntity.ok(Map.of(
                    "licensePlateExists", exists
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking license plate"));
        }
    }

}
