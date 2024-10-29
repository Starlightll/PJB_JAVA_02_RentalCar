package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.helper.Helper;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarStatusRepository;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import com.rentalcar.rentalcar.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/my-cars")
    public String myCar() {
        return "/carowner/MyCars";
    }

    @GetMapping("/check-draft")
    public ResponseEntity<?> addCar(HttpSession session) {
        User user = (User) session.getAttribute("user");
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if(carDraft != null) {
            return ResponseEntity.ok(Map.of("hasDraft", true));
        }
        return ResponseEntity.ok(Map.of("hasDraft", false));
    }

    @GetMapping("/add-car")
    public String addCar(Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("additionalFunction", additionalFunctionRepository.findAll());
        model.addAttribute("carStatus", carStatusRepository.findAll());
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if(carDraft != null) {
            model.addAttribute("carDraft", carDraft);
        } else {
            carDraftRepository.deleteDraftByUserId(user.getId());
            carDraft = new CarDraft();
            carDraft.setUser(user);
            carDraftRepository.save(carDraft);
            model.addAttribute("carDraft", carDraft);
        }
        return "carowner/AddCar";
    }


    @PostMapping("/create-draft")
    public void createDraft(){
        User user = (User) httpSession.getAttribute("user");

    }

//    @PostMapping("/save-draft")
//    @ResponseBody
//    public void saveDraft(@RequestBody CarDraft carDraft, HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        carDraft.setUser(user);
//        carDraftService.saveDraft(carDraft, user);
//    }

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

    // Get the user ID and create a folder based on userId + draftId
    User user = (User) session.getAttribute("user");
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    // Get or create draft ID
    CarDraft existingDraft = carDraftService.getDraftByLastModified(user.getId());
    String draftId = existingDraft != null ? existingDraft.getDraftId().toString() : UUID.randomUUID().toString();

    // Create folder path
    String folderName = String.format("%s_%d_%s", user.getUsername(), user.getId(), draftId);
    Path draftFolderPath = Paths.get("uploads", folderName);

    try {
        // Store each file if it exists
        if (frontImage != null && !frontImage.isEmpty() && frontImage.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(frontImage, draftFolderPath);
            carDraft.setFrontImage(storedPath);
        }

        if (backImage != null && !backImage.isEmpty() && backImage.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(backImage, draftFolderPath);
            carDraft.setBackImage(storedPath);
        }

        if (leftImage != null && !leftImage.isEmpty() && leftImage.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(leftImage, draftFolderPath);
            carDraft.setLeftImage(storedPath);
        }

        if (rightImage != null && !rightImage.isEmpty() && rightImage.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(rightImage, draftFolderPath);
            carDraft.setRightImage(storedPath);
        }

        if (registration != null && !registration.isEmpty() && registration.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(registration, draftFolderPath);
            carDraft.setRegistration(storedPath);
        }

        if (certificate != null && !certificate.isEmpty() && certificate.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(certificate, draftFolderPath);
            carDraft.setCertificate(storedPath);
        }

        if (insurance != null && !insurance.isEmpty() && insurance.getSize() > 0) {
            String storedPath = fileStorageService.storeFile(insurance, draftFolderPath);
            carDraft.setInsurance(storedPath);
        }

        carDraftService.saveDraft(carDraft, user);

        return ResponseEntity.ok("Draft saved successfully");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error saving draft: " + e.getMessage());
    }
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
