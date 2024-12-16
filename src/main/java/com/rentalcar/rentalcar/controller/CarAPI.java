package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.mappers.CarDraftMapper;
import com.rentalcar.rentalcar.mappers.CarMapper;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.CarDraftService;
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

/**
 * CarAPI is a REST controller that provides endpoints to manage car and car updates.
 *
 * This controller handles several operations related to cars such as checking the ownership
 * of a license plate, retrieving cars by their status, approving car entry,
 * managing car update requests, and sending notifications via email.
 *
 * Dependencies are injected using Spring's @Autowired annotation, and it uses a combination
 * of repositories, mappers, and services to perform operations.
 */
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
    @Autowired
    private CarDraftRepository carDraftRepository;
    @Autowired
    private CarDraftMapper carDraftMapper;
    @Autowired
    private CarDraftService carDraftService;


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
            Map<String, Object> variables = carApproveForm();
            emailService.sendEmailApprovedCar(car.getUser().getEmail(), "Car Approve", variables);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating car status"));
        }
    }

    @GetMapping("/get-list-car-request-update")
    public ResponseEntity<?> getListCarRequestUpdate() {
        return ResponseEntity.ok(carDraftRepository.findCarDraftsByVerifyStatus("Pending")
                .stream().map(carDraftMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/approve-car-update")
    public ResponseEntity<?> approveCarUpdate(@RequestParam Integer draftId) {
        try {
            CarDraft carDraft = carDraftRepository.findCarDraftsByDraftIdAndVerifyStatus(draftId, "Pending");
            if (carDraft == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Car update request not found"));
            }
            if(carDraftService.approveCarUpdateRequest(draftId, carDraft.getUser())){
                Map<String, Object> variables = carUpdateApprove();
                emailService.sendEmailApprovedCar(carDraft.getUser().getEmail(), "Car Update Approved", variables);
                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error approving car update"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Error approving car update"));
    }

    @GetMapping("/reject-car-update")
    public ResponseEntity<?> rejectCarUpdate(@RequestParam Integer draftId) {
        try {
            CarDraft carDraft = carDraftRepository.findCarDraftsByDraftIdAndVerifyStatus(draftId, "Pending");
            if (carDraft == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Car update request not found"));
            }
            if(carDraftService.rejectCarUpdateRequest(draftId, carDraft.getUser())){
                Map<String, Object> variables = carUpdateReject();
                emailService.sendEmailApprovedCar(carDraft.getUser().getEmail(), "Car Update Rejected", variables);
                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error rejecting car update"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Error rejecting car update"));
    }

    @GetMapping("/cancel-car-update")
    public ResponseEntity<?> cancelCarUpdate(@RequestParam Integer draftId) {
        try {
            CarDraft carDraft = carDraftRepository.findCarDraftsByDraftIdAndVerifyStatus(draftId, "Pending");
            if (carDraft == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Car update request not found"));
            }
            if(carDraftService.cancelCarUpdateRequest(draftId, carDraft.getUser())){
                return ResponseEntity.ok(Map.of("success", true));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error cancel car update"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Error cancel car update"));
    }


    private static Map<String, Object> carApproveForm() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("title1", "Bravo!");
        variables.put("title2", "Your Car is Approved and Ready to Rent.");
        variables.put("body", "Thank you for your submission. Your car is now approved and ready to rent. Please make sure to keep your car in good condition and follow the rules. If you have any questions, please contact us.");
        return variables;
    }

    private static Map<String, Object> carUpdateApprove() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("title1", "Bravo!");
        variables.put("title2", "Your update request is approved.");
        variables.put("body", "Thank you for your submission. Your car update request is now approved. Please make sure to keep your car in good condition and follow the rules. If you have any questions, please contact us.");
        return variables;
    }

    private static Map<String, Object> carUpdateReject() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("title1", "Sorry!");
        variables.put("title2", "Your update request is rejected.");
        variables.put("body", "Your car update request is rejected. Please make sure to keep your car information up to date and follow the rules. If you have any questions, please contact us.");
        return variables;
    }


}
