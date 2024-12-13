package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.CarDraftService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.*;

/**
 * CarDraftController is responsible for handling requests related to car draft operations.
 * It interacts with the CarDraftService to check, save, or delete car drafts for users.
 */
@Controller
@RequestMapping("car-draft")
public class CarDraftController {

    @Autowired
    CarDraftService carDraftService;

    @Autowired
    private CarDraftRepository carDraftRepository;

    @Autowired
    CarRepository carRepository;

    @GetMapping("/check-draft")
    public ResponseEntity<?> checkDraft(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if (carDraft != null) {
            return ResponseEntity.ok(Map.of(
                    "hasDraft", true,
                    "lastModified", carDraft.getLastModified() != null ? carDraft.getLastModified().toString() : "NULL"
            ));
        }
        return ResponseEntity.ok(Map.of("hasDraft", false));
    }




    @PostMapping("/save-draft")
    public ResponseEntity<?> saveDraft(
            @RequestParam(value = "carDraft") String carDraftJson,
            @RequestParam(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestParam(value = "backImage", required = false) MultipartFile backImage,
            @RequestParam(value = "leftImage", required = false) MultipartFile leftImage,
            @RequestParam(value = "rightImage", required = false) MultipartFile rightImage,
            @RequestParam(value = "registration", required = false) MultipartFile registration,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate,
            @RequestParam(value = "insurance", required = false) MultipartFile insurance,
            HttpSession session
    ) throws IOException {
        // Parse carDraft JSON
        ObjectMapper objectMapper = new ObjectMapper();
        CarDraft carDraft = objectMapper.readValue(carDraftJson, CarDraft.class);
        //Validate here
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedBasePrice = df.format(carDraft.getBasePrice() == null ? 0 : carDraft.getBasePrice());
            String formattedCarPrice = df.format(carDraft.getCarPrice() == null ? 0 : carDraft.getCarPrice());
            String formattedDeposit = df.format(carDraft.getDeposit() == null ? 0 : carDraft.getDeposit());
            String formattedMileage = df.format(carDraft.getMileage() == null ? 0 : carDraft.getMileage());
            String formattedFuelConsumption = df.format(carDraft.getFuelConsumption() == null ? 0 : carDraft.getFuelConsumption());
            Double carPrice = carDraft.getCarPrice();
            Pattern pattern = Pattern.compile(MONEY_REGEX);
            Matcher matcher = pattern.matcher(formattedCarPrice);
            if (carPrice < 0 || !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid car price");
            }
            Double basePrice = carDraft.getBasePrice();
            matcher = pattern.matcher(formattedBasePrice);
            if (basePrice < 0 || !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid base price");
            }
            Double deposit = carDraft.getDeposit();
            matcher = pattern.matcher(formattedDeposit);
            if (deposit < 0  || !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid deposit");
            }
            Double mileage = carDraft.getMileage();
            pattern = Pattern.compile(DISTANCE_REGEX);
            matcher = pattern.matcher(formattedMileage);
            if (mileage < 0 || mileage > 1000000 || !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mileage");
            }
            Double fuelConsumption = carDraft.getFuelConsumption();
            pattern = Pattern.compile(FUEL_CONSUMPTION_REGEX);
            matcher = pattern.matcher(formattedFuelConsumption);
            if (fuelConsumption < 0 || fuelConsumption > 10000 || !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid fuel consumption");
            }
            String licensePlate = carDraft.getLicensePlate();
            pattern = Pattern.compile(LICENSE_PLATE_REGEX);
            matcher = pattern.matcher(licensePlate);
            if (!licensePlate.isEmpty() && !matcher.matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid license plate");
            }else{
                Long userOwnLicence = carRepository.findFirstUserByLicensePlate(licensePlate);
                if(userOwnLicence != null) {
                    User user = (User) session.getAttribute("user");
                    if(!Objects.equals(userOwnLicence, user.getId())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Liscense plate already owned by another user");
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }

        User user = (User) session.getAttribute("user");

        //Save draft
        MultipartFile[] files = {frontImage, backImage, leftImage, rightImage, registration, certificate, insurance};
        carDraftService.saveDraft(carDraft, files, user);
        return ResponseEntity.ok("Draft saved successfully");
    }


    @PostMapping("/delete-draft")
    @ResponseBody
    public ResponseEntity<?> deleteDraft(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        try {
            carDraftService.deleteDraftByUserId(user.getId());
            return ResponseEntity.ok("Draft deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting draft");
        }
    }
}
