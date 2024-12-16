package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import com.rentalcar.rentalcar.service.RentalCarService;
import com.rentalcar.rentalcar.service.RevenueService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.*;

/**
 * The CarOwnerController class provides endpoints for managing car-related operations
 * for car owners. This includes functionalities to add, edit, update, delete cars,
 * and handle various car status changes and requests.
 */
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
    @Autowired
    private RentalCarService rentalCarService;
    private CarDraftRepository carDraftRepository;

    @Autowired
    private RevenueService revenueService ;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TransactionRepository transactionRepository;

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
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Car> carPage;
        if (findByStatus) {
            int statusId = 0;
            if (sortBy.equalsIgnoreCase("available")) {
                statusId = 1;
            } else {
                statusId = 2;
            }
            pageable = PageRequest.of(page - 1, size);
            carPage = carRepository.findAllByCarStatusAndUser(statusId, user.getId(), pageable);
        } else {
            List<Integer> statusIds = List.of(1, 2, 3, 5, 6, 8, 10, 11, 14, 15, 16);
            carPage = carRepository.findAllByCarStatus_StatusIdInAndUserId(statusIds, user.getId(), pageable);
        }
        List<Car> cars = carPage.getContent();
        List<CarDto> carDTOs = new ArrayList<>();
        for (Car car : cars) {
            CarDto car_dto = carOwnerService.getRatingByCarId(Long.valueOf(car.getCarId()));
            carDTOs.add(new CarDto(car, car_dto.getAverageRating()));
        }

        if (cars.isEmpty()) {
            model.addAttribute("message", "You have no cars");
        }
        model.addAttribute("carList", cars);
        model.addAttribute("carDTOList", carDTOs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);
        model.addAttribute("size", size);

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
        CarDto carRating = carOwnerService.getRatingByCarId(Long.valueOf(carId));
        // Check if car is not found or car is deleted
        if (car == null || car.getCarStatus().getStatusId() == 4) {
            return "redirect:/car-owner/my-cars";
        } else {
            // Check if user is allowed to edit this car
            if (!Objects.equals(car.getUser().getId(), ((User) session.getAttribute("user")).getId())) {
                throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
            }
            model.addAttribute("carRating", carRating.getAverageRating());
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
            List<Integer> statusIds = List.of(1, 2, 3);
            List<CarStatus> carStatus = carStatusRepository.findCarStatusesByStatusIdIsIn(statusIds);
            model.addAttribute("carStatuses", carStatus);

            //Check car has requestChangeBasicInformation
            CarDraft carDraft = carDraftService.getDraftOfRequestChangeBasicInformation(car.getUser().getId(), carId, "Pending");
            if (carDraft != null) {
                model.addAttribute("carDraft", carDraft);
            }

            //Check if car has booking
            List<Integer> bookingStatus = carRepository.findBookingStatusIdByCarId(carId);
            if (bookingStatus.contains(1)) {
                model.addAttribute("bookingStatus", 1);
            } else if (bookingStatus.contains(4)) {
                model.addAttribute("bookingStatus", 4);
            } else {
                model.addAttribute("bookingStatus", 0);

            }
        }
        return "carowner/EditCar";
    }

    @PostMapping("/add-car")
    public ResponseEntity<?> addCar(
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        CarDraft carDraft = carDraftService.getDraftByLastModified(user.getId());
        if (carDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Draft not found");
        }
        try {
            carOwnerService.addCar(carDraft, user);
            return ResponseEntity.ok("Car saved successfully");
        } catch (Exception e) {
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
        Car car = carRepository.getCarByCarId(carId);
        User user = (User) session.getAttribute("user");

        // Check if user is allowed to update this car
        Integer carStatusId = car.getCarStatus().getStatusId();
        if (!Objects.equals(car.getUser().getId(), user.getId()) || (carStatusId != 1 && carStatusId != 3) || (car.getCarStatus().getStatusId() == 8 && user.getRoles().get(0).getId() != 1)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not allowed to update this car");
        }

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
            if (deposit < 0 || !matcher.matches()) {
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

        // Convert carDraft to car
        Car carUpdate = carDraftService.convertCarDraftToCar(carDraft);

        //Update car
        MultipartFile[] files = {frontImage, backImage, leftImage, rightImage};
        carOwnerService.updateCar(carUpdate, files, user, carId, carStatus);
        return ResponseEntity.ok("Car updated successfully");
    }

    @PostMapping("/request-change-basic-information")
    public ResponseEntity<?> requestChangeBasicInformation(
            @RequestParam(value = "carId") Integer carId,
            @RequestParam(value = "licensePlate") String licensePlate,
            @RequestParam(value = "model") String model,
            @RequestParam(value = "color") String color,
            @RequestParam(value = "productionYear") Integer productionYear,
            @RequestParam(value = "seatNo") Integer seatNo,
            @RequestParam(value = "transmissionType") String transmissionType,
            @RequestParam(value = "fuelType") String fuelType,
            @RequestParam(value = "registration", required = false) MultipartFile registration,
            @RequestParam(value = "certificate", required = false) MultipartFile certificate,
            @RequestParam(value = "insurance", required = false) MultipartFile insurance,
            HttpSession session
    ) throws IOException {
        User user = (User) session.getAttribute("user");
        Car car = carRepository.getCarByCarId(carId);
        if (car == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }
        List<Integer> statusIds = List.of(1, 3);
        if (!Objects.equals(car.getUser().getId(), user.getId()) || !statusIds.contains(car.getCarStatus().getStatusId())) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }
        if (licensePlate == null || model == null || color == null || productionYear == null || seatNo == null || transmissionType == null || fuelType == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }


        CarDraft carDraft = carDraftService.convertCarToCarDraft(car);
        carDraft.setLicensePlate(licensePlate);
        carDraft.setModel(model);
        carDraft.setColor(color);
        carDraft.setProductionYear(productionYear);
        carDraft.setSeat(seatNo);
        carDraft.setTransmission(transmissionType);
        carDraft.setFuelType(fuelType);

        MultipartFile[] files = {registration, certificate, insurance};
        //Create CarDraft and save here - I will do it tomorrow
        if (carOwnerService.requestChangeBasicInformation(carDraft, files, user, carId)) {
            return ResponseEntity.ok("Request sent successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't send request");
    }


    @GetMapping("/delete-car")
    public ResponseEntity<?> deleteCar(
            @RequestParam("carId") Integer carId,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        Car car = carRepository.getCarByCarId(carId);
        if (car == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }
        if (!Objects.equals(car.getUser().getId(), user.getId()) || car.getCarStatus().getStatusId() != 8) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to delete this car");
        }
        carOwnerService.deleteCar(carId);
        return ResponseEntity.ok("Car deleted successfully");
    }


    @GetMapping("/confirm-deposit")
    public String confirmDeposit(@RequestParam("carId") Long carId,
                                 HttpSession session,
                                 Model model) {
        boolean isConfirmDeposit = rentalCarService.confirmDepositCar(carId, session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "The Car deposit has been confirmed.");
        } else {
            model.addAttribute("error", "Unable to confirm deposit the booking. Please try again!!");
        }
        return editCar(carId.intValue(), model, session);
    }

    @GetMapping("/confirm-cancel")
    public String confirmCancel(@RequestParam("carId") Long carId,
                                HttpSession session,
                                Model model) {
        boolean isConfirmDeposit = rentalCarService.confirmCancelBookingCar(carId, session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "Confirm booking cancellation and refund deposit to customer!");
        } else {
            model.addAttribute("error", "Unable to confirm cancel the booking. Please try again!!");
        }
        return editCar(carId.intValue(), model, session);
    }

    @GetMapping("/confirm-deposit-mycar")
    public String confirmDepositmyCar(@RequestParam("carId") Long carId,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      @RequestParam(defaultValue = "lastModified") String sortBy,
                                      @RequestParam(defaultValue = "desc") String order,
                                      HttpSession session,
                                      Model model) {
        boolean isConfirmDeposit = rentalCarService.confirmDepositCar(carId, session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "The Car deposit has been confirmed.");
        } else {
            model.addAttribute("error", "Unable to confirm deposit the booking. Please try again!!");
        }
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
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Car> carPage;
        if (findByStatus) {
            int statusId = 0;
            if (sortBy.equalsIgnoreCase("available")) {
                statusId = 1;
            } else {
                statusId = 2;
            }
            pageable = PageRequest.of(page - 1, size);
            carPage = carRepository.findAllByCarStatusAndUser(statusId, user.getId(), pageable);
        } else {
            carPage = carRepository.findAllByUser(user, pageable);
        }
        List<Car> cars = carPage.getContent();
        List<CarDto> carDTOs = new ArrayList<>();
        for (Car car : cars) {
            CarDto car_dto = carOwnerService.getRatingByCarId(Long.valueOf(car.getCarId()));
            carDTOs.add(new CarDto(car, car_dto.getAverageRating()));
        }
        model.addAttribute("carDTOList", carDTOs);
        if (cars.isEmpty()) {
            model.addAttribute("message", "You have no cars");
        } else {
            model.addAttribute("carList", cars);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
        }
        return "/carowner/MyCars";

    }

    @GetMapping("/confirm-cancel-mycar")
    public String confirmCancelmyCar(@RequestParam("carId") Long carId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "lastModified") String sortBy,
                                     @RequestParam(defaultValue = "desc") String order,
                                     HttpSession session,
                                     Model model) {
        boolean isConfirmDeposit = rentalCarService.confirmCancelBookingCar(carId, session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "Confirm booking cancellation and refund deposit to customer!");
        } else {
            model.addAttribute("error", "Unable to confirm cancel the booking. Please try again!!");
        }
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
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Car> carPage;
        if (findByStatus) {
            int statusId = 0;
            if (sortBy.equalsIgnoreCase("available")) {
                statusId = 1;
            } else {
                statusId = 2;
            }
            pageable = PageRequest.of(page - 1, size);
            carPage = carRepository.findAllByCarStatusAndUser(statusId, user.getId(), pageable);
        } else {
            carPage = carRepository.findAllByUser(user, pageable);
        }
        List<Car> cars = carPage.getContent();
        List<CarDto> carDTOs = new ArrayList<>();
        for (Car car : cars) {
            CarDto car_dto = carOwnerService.getRatingByCarId(Long.valueOf(car.getCarId()));
            carDTOs.add(new CarDto(car, car_dto.getAverageRating()));
        }
        model.addAttribute("carDTOList", carDTOs);
        if (cars.isEmpty()) {
            model.addAttribute("message", "You have no cars");
        } else {
            model.addAttribute("carList", cars);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
        }
        return "/carowner/MyCars";

    }

    @GetMapping("/check-return")
    public ResponseEntity<?> checkReturn(@RequestParam("carId") Long carId,
                                         HttpSession session) {
        Map<String, String> response = rentalCarService.checkReturnCar(carId, session);

        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/confirm-return-car")
    public ResponseEntity<Map<String, Object>> confirmReturnCar(@RequestParam("carId") Long carId,
                                                                HttpSession session,
                                                                Model model) {
        User user = (User) session.getAttribute("user");

        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            return generateResponse(response, "error", "User not found in session.");
        }

        int casePayment = rentalCarService.confirmReturnCar(carId, session);

        // Handle response based on caseReturn value
        if (casePayment == 1) {
            return generateResponse(response, "success1", "Booking has been successfully completed.");
        } else if (casePayment == -1) {
            return generateResponse(response, "success2", "Not enough money! Please top up your wallet and try again!!!");
        } else if (casePayment == 2) {
            return generateResponse(response, "success3", "Waiting customer payment!");
        }

        Car car = carRepository.getCarByCarId(carId.intValue());

        if (car == null) {

            return generateResponse(response, "error", "Error Return!");
        } else {
            model.addAttribute("car", car);
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

            List<Integer> bookingStatus = carRepository.findBookingStatusIdByCarId(carId.intValue());
            if (bookingStatus.contains(1)) {
                model.addAttribute("bookingStatus", 1);
            } else if (bookingStatus.contains(4)) {
                model.addAttribute("bookingStatus", 4);
            } else {
                model.addAttribute("bookingStatus", 0);

            }
        }
        return generateResponse(response, "error", "Error Return!");
    }


    @GetMapping("/check-payment")
    public ResponseEntity<?> checkPayment(@RequestParam("carId") Long carId,
                                          HttpSession session) {
        Map<String, String> response = rentalCarService.checkReturnCar(carId, session);

        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private ResponseEntity<Map<String, Object>> generateResponse(Map<String, Object> response, String status, String message) {
        response.put("status", status);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue")
    public Object getRevenuePage(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "week", required = false) String week,
            Model model,
            HttpSession session,
            HttpServletRequest request) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Long userId = user.getId();
        year = (year != null) ? year : LocalDate.now().getYear();

        Map<String, Double> revenueData = getRevenueData(userId, year, month, week);

        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();
        int lastMonth = (thisMonth == 1) ? 12 : thisMonth - 1;

        BigDecimal totalRevenueYear = calculateTotalRevenue(revenueService.getYearlyRevenue(userId, thisYear));
        BigDecimal totalRevenueMonth = calculateTotalRevenue(revenueService.getMonthlyRevenue(userId, thisYear, thisMonth));
        BigDecimal totalLastRevenueMonth = calculateTotalRevenue(revenueService.getMonthlyRevenue(userId, lastMonth == 12 ? thisYear - 1 : thisYear, lastMonth));
        BigDecimal rateComparison = calculateComparisonRate(totalRevenueMonth, totalLastRevenueMonth);

        int monthlyBookings = bookingRepository.countBookingsInMonth(thisYear, thisMonth);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("labels", revenueData.keySet());
            responseData.put("data", revenueData.values());
            responseData.put("totalRevenueYear", formatRevenue(totalRevenueYear));
            responseData.put("totalRevenueMonth", formatRevenue(totalRevenueMonth));
            responseData.put("revenueComparison", rateComparison.doubleValue());
            responseData.put("monthlyBookings", monthlyBookings);

            return ResponseEntity.ok(responseData);
        }

        LocalDateTime startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfThisMonth = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        Map<String, BigDecimal> pieChartData = new HashMap<>();
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startOfThisMonth, endOfThisMonth);

        for (Transaction transaction : transactions) {
            if (!pieChartData.containsKey(transaction.getTransactionType())) {
                pieChartData.put(transaction.getTransactionType(), transaction.getAmount());
            } else {
                BigDecimal currentAmount = pieChartData.get(transaction.getTransactionType());
                pieChartData.put(transaction.getTransactionType(), currentAmount.add(transaction.getAmount()));
            }
        }


        model.addAttribute("transactions", transactions);
        model.addAttribute("monthlyBookings", monthlyBookings);
        model.addAttribute("totalRevenueYear", formatRevenue(totalRevenueYear));
        model.addAttribute("totalRevenueMonth", formatRevenue(totalRevenueMonth));
        model.addAttribute("revenueComparison", rateComparison.doubleValue());
        model.addAttribute("year", year);
        model.addAttribute("month", month != null ? month : "all");
        model.addAttribute("week", week != null ? week : "all");
        model.addAttribute("labels", revenueData.keySet());
        model.addAttribute("data", revenueData.values());
        model.addAttribute("pieLabels", pieChartData.keySet());
        model.addAttribute("pieData", pieChartData.values());

        return "carowner/revenue";
    }


    private BigDecimal calculateTotalRevenue(Map<String, Double> revenue) {
        return revenue.values().stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateComparisonRate(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) != 0) {
            return current.multiply(BigDecimal.valueOf(100))
                    .divide(previous, 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(100);
    }

    private String formatRevenue(BigDecimal revenue) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(revenue != null ? revenue.intValue() : BigDecimal.ZERO);
    }

    private Map<String, Double> getRevenueData(Long userId, Integer year, String month, String week) {
        if (month == null || "all".equalsIgnoreCase(month)) {
            if (week == null || "all".equalsIgnoreCase(week)) {
                return revenueService.getYearlyRevenue(userId, year);
            } else {
                int selectedWeek = Integer.parseInt(week);
                return revenueService.getWeeklyRevenue(userId, year, 1, selectedWeek);
            }
        } else {
            int selectedMonth = Integer.parseInt(month);
            if (week == null || "all".equalsIgnoreCase(week)) {
                return revenueService.getMonthlyRevenue(userId, year, selectedMonth);
            } else {
                int selectedWeek = Integer.parseInt(week);
                return revenueService.getWeeklyRevenue(userId, year, selectedMonth, selectedWeek);
            }
        }
    }


}
