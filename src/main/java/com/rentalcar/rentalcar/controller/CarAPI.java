package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("carAPI")
public class CarAPI {

    private final CarRepository carRepository;

    public CarAPI(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

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

}
