package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import com.rentalcar.rentalcar.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
    private CarRepository carRepository;


    @GetMapping("/my-cars")
    public String myCar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "lastModified") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            Model model) {

        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Car> carPage = carRepository.findAll(pageable);
        List<Car> cars = carPage.getContent();

        if(cars.isEmpty()){
            model.addAttribute("message", "You have no cars");
        }else{
            model.addAttribute("carList", cars);
        }
        return "/carowner/MyCars";
    }

    //Display the add car page
    @GetMapping("/add-car")
    public String addCar(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("additionalFunction", additionalFunctionRepository.findAll());
        model.addAttribute("carStatus", carStatusRepository.findAll());
        CarDraft carDraft = carDraftService.createCarDraft(user);
        model.addAttribute("carDraft", carDraft);
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedBasePrice = df.format(carDraft.getBasePrice() == null ? 0 : carDraft.getBasePrice());
        String formattedCarPrice = df.format(carDraft.getCarPrice() == null ? 0 : carDraft.getCarPrice());
        String formattedDeposit = df.format(carDraft.getDeposit() == null ? 0 : carDraft.getDeposit());
        String formattedMileage = df.format(carDraft.getMileage() == null ? 0 : carDraft.getMileage());
        String formattedFuelConsumption = df.format(carDraft.getFuelConsumption() == null ? 0 : carDraft.getFuelConsumption());
        model.addAttribute("formattedCarPrice", formattedCarPrice);
        model.addAttribute("formattedDeposit", formattedDeposit);
        model.addAttribute("formattedMileage", formattedMileage);
        model.addAttribute("formattedFuelConsumption", formattedFuelConsumption);
        model.addAttribute("formattedBasePrice", formattedBasePrice);
        return "carowner/AddCar";
    }


    @GetMapping("get-car")
    @ResponseBody
    public CarDraft getCar() {
        return new CarDraft();
    }

    @GetMapping("edit-car/{carId}")
    public String editCar(@PathVariable("carId") Integer carId, Model model, HttpSession session) {
        Car car = carRepository.getCarByCarId(carId);
        if(car == null){
            return "redirect:/my-cars";
        }else{
            User user = (User) session.getAttribute("user");
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("additionalFunction", additionalFunctionRepository.findAll());
            model.addAttribute("carStatus", carStatusRepository.findAll());
            model.addAttribute("car", car);
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedBasePrice = df.format(car.getBasePrice() == null ? 0 : car.getBasePrice());
            String formattedCarPrice = df.format(car.getCarPrice() == null ? 0 : car.getCarPrice());
            String formattedDeposit = df.format(car.getDeposit() == null ? 0 : car.getDeposit());
            String formattedMileage = df.format(car.getMileage() == null ? 0 : car.getMileage());
            String formattedFuelConsumption = df.format(car.getFuelConsumption() == null ? 0 : car.getFuelConsumption());
            model.addAttribute("formattedCarPrice", formattedCarPrice);
            model.addAttribute("formattedDeposit", formattedDeposit);
            model.addAttribute("formattedMileage", formattedMileage);
            model.addAttribute("formattedFuelConsumption", formattedFuelConsumption);
            model.addAttribute("formattedBasePrice", formattedBasePrice);
            String registrationPath = car.getRegistration();
            String certificatePath = car.getCertificate();
            String insurancePath = car.getInsurance();
            model.addAttribute("registrationUrl", "/" + registrationPath);
            model.addAttribute("certificateUrl", "/" + certificatePath);
            model.addAttribute("insuranceUrl", "/" + insurancePath);

        }
        return "carowner/EditCar";
    }

    @PostMapping("/save-car")
    public ResponseEntity<?> saveCar(
            HttpSession session
    ) throws IOException {
        User user = (User) session.getAttribute("user");
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if (carDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Draft not found");
        }
        try{
            carOwnerService.saveCar(carDraft, user);
            return ResponseEntity.ok("Car saved successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't saving car");
        }
    }

//    @PostMapping("/save-car")
//    public String saveCar(
//            HttpSession session,
//            Model model
//    ) throws IOException {
//        User user = (User) session.getAttribute("user");
//        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
//        if (carDraft == null) {
//            return "carowner/MyCars";
//        }else{
//            try{
//                carOwnerService.saveCar(carDraft, user);
//                model.addAttribute("success", true);
//                return "carowner/MyCars";
//            }catch (Exception e){
//                model.addAttribute("error", true);
//                return "carowner/MyCars";
//            }
//        }
//    }


    @GetMapping("/delete-car")
    public String deleteCar() {
        return "carowner/MyCars";
    }



}
