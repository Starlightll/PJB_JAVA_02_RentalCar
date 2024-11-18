package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
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
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.*;
import static com.rentalcar.rentalcar.common.Regex.LICENSE_PLATE_REGEX;

@Controller
@RequestMapping("/car-owner")
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "lastModified") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean findByStatus = false;
        switch (sortBy) {
            case "newestToLatest":
                sortBy = "lastModified";
                order = "desc";
                break;
            case "latestToNewest":
                sortBy = "lastModified";
                order = "asc";
                break;
            case "priceLowToHigh":
                sortBy = "basePrice";
                order = "asc";
                break;
            case "priceHighToLow":
                sortBy = "basePrice";
                order = "desc";
                break;
            case "available", "booked":
                findByStatus = true;
                break;
            default:
                break;
        }
        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        Page<Car> carPage;
        if(findByStatus){
            int statusId = 0;
            if(sortBy.equalsIgnoreCase("available")){
                statusId = 1;
            }else{
                statusId = 2;
            }
            pageable = PageRequest.of(page-1, size);
            carPage = carRepository.findAllByCarStatusAndUser(statusId, user.getId(), pageable);
        }else{
            carPage = carRepository.findAllByUser(user, pageable);
        }
        List<Car> cars = carPage.getContent();
        if(cars.isEmpty()){
            model.addAttribute("message", "You have no cars");
        }else{
            model.addAttribute("carList", cars);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
                model.addAttribute("size", size);
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
            return "redirect:/car-owner/my-cars";
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
            List<CarStatus> carStatus = carStatusRepository.findAll();
            model.addAttribute("carStatuses", carStatus);

        }
        return "carowner/EditCar";
    }

    @PostMapping("/add-car")
    public ResponseEntity<?> addCar(
            HttpSession session
    ) throws IOException {
        User user = (User) session.getAttribute("user");
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if (carDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Draft not found");
        }
        try{
            carOwnerService.addCar(carDraft, user);
            return ResponseEntity.ok("Car saved successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't saving car");
        }
    }

    @PostMapping("/update-car")
    public ResponseEntity<?> updateCar(
            @RequestParam(value = "carId") Integer carId,
            @RequestParam(value = "carStatus") Integer carStatus,
            @RequestParam(value = "carData") String carJson,
            @RequestParam(value = "frontImage", required = false) MultipartFile frontImage,
            @RequestParam(value = "backImage", required = false) MultipartFile backImage,
            @RequestParam(value = "leftImage", required = false) MultipartFile leftImage,
            @RequestParam(value = "rightImage", required = false) MultipartFile rightImage,
            HttpSession session
    ) throws IOException {
        // Parse carDraft JSON
        ObjectMapper objectMapper = new ObjectMapper();
        CarDraft carDraft = objectMapper.readValue(carJson, CarDraft.class);
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }

        User user = (User) session.getAttribute("user");
        Car carUpdate = carDraftService.convertCarDraftToCar(carDraft);
        //Update car
        MultipartFile[] files = {frontImage, backImage, leftImage, rightImage};
        carOwnerService.updateCar(carUpdate, files, user, carId, carStatus);
        return ResponseEntity.ok("Car updated successfully");
    }


    @GetMapping("/delete-car")
    public String deleteCar() {
        return "carowner/MyCars";
    }



}
