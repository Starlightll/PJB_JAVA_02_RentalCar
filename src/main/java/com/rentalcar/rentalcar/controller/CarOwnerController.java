package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarStatusRepository;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import com.rentalcar.rentalcar.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

@Controller
public class CarOwnerController {
    @Autowired
    CarOwnerService carOwnerService;
    @Autowired
    CarDraftService carDraftService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private CarStatusRepository carStatusRepository;
    @Autowired
    private CarDraftRepository carDraftRepository;


    @GetMapping("/my-cars")
    public String myCar() {
        return "/carowner/MyCars";
    }

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

    @GetMapping("/add-car")
    public String addCar(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("additionalFunction", additionalFunctionRepository.findAll());
        model.addAttribute("carStatus", carStatusRepository.findAll());
        CarDraft carDraft = carDraftService.createCarDraft(user);
        model.addAttribute("carDraft", carDraft);
        DecimalFormat df = new DecimalFormat("#.############");
        String formattedBasePrice = df.format(carDraft.getBasePrice() == null ? 0 : carDraft.getBasePrice());
        String formattedCarPrice = df.format(carDraft.getCarPrice() == null ? 0 : carDraft.getBasePrice());
        String formattedDeposit = df.format(carDraft.getDeposit() == null ? 0 : carDraft.getBasePrice());
        String formattedMileage = df.format(carDraft.getMileage() == null ? 0 : carDraft.getBasePrice());
        String formattedFuelConsumption = df.format(carDraft.getFuelConsumption() == null ? 0 : carDraft.getBasePrice());
        model.addAttribute("formattedCarPrice", formattedCarPrice);
        model.addAttribute("formattedDeposit", formattedDeposit);
        model.addAttribute("formattedMileage", formattedMileage);
        model.addAttribute("formattedFuelConsumption", formattedFuelConsumption);
        model.addAttribute("formattedBasePrice", formattedBasePrice);

        return "carowner/AddCar";
    }


    @PostMapping("/create-draft")
    public void createDraft() {
        User user = (User) httpSession.getAttribute("user");

    }

    //    Test saveDraft with multipart file
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

        User user = (User) session.getAttribute("user");

        //Save draft
        MultipartFile[] files = {frontImage, backImage, leftImage, rightImage, registration, certificate, insurance};
        carDraftService.saveDraft(carDraft, files, user);
        return ResponseEntity.ok("Draft saved successfully");
    }


    @GetMapping("get-car")
    @ResponseBody
    public CarDraft getCar() {
        return new CarDraft();
    }

    @PostMapping("/delete-draft")
    @ResponseBody
    public ResponseEntity<?> deleteDraft() {
        User user = (User) httpSession.getAttribute("user");
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

    @GetMapping("/edit-car")
    public String editCar() {
        return "carowner/EditCar";
    }

    @GetMapping("/delete-car")
    public String deleteCar() {
        return "carowner/MyCars";
    }



}
