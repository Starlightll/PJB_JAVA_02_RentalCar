package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rentalcar.rentalcar.common.CalculateNumberOfDays;
import com.rentalcar.rentalcar.common.Regex;
import com.rentalcar.rentalcar.dto.*;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.RentalCarService;
import com.rentalcar.rentalcar.service.ViewEditBookingService;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.EMAIL_REGEX;

@Controller
public class BookingDetailsController {


    @Autowired RentalCarService rentalCarService;

    @Autowired UserRepo userRepo;

    @Autowired private CarRepository carRepository;

    @Autowired private BrandRepository brandRepository;

    @Autowired private AdditionalFunctionRepository additionalFunctionRepository;

    @Autowired private CarStatusRepository carStatusRepository;

    @Autowired private ViewEditBookingService viewEditBookingService;

    @Autowired private DriverDetailRepository driverDetailRepository;

    @Autowired RatingStarRepository ratingStarRepo;


    @GetMapping({"/customer/booking-detail", "/car-owner/booking-detail"})
    public String bookingDetail(@RequestParam Integer bookingId,@RequestParam Integer carId, @RequestParam Integer userId ,@RequestParam String navigate,  Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        User rentUser = userRepo.getUserById(Long.valueOf(userId));
        MyBookingDto booking = new MyBookingDto();

        Car car = carRepository.getCarByCarId(carId);
        if(car == null) {
            return "redirect:/";
        }
        try {
             booking = viewEditBookingService.getBookingDetail(bookingId, carId, session, userId);
        } catch (RuntimeException e) {
            switch (e.getMessage()) {
                case "user not found":
                    return "redirect:/login";
                case "booking detail not found":
                    return "redirect:/customer/my-bookings";
            }
        }
        List<UserDto> listDriver = getAllDriverAvailable(bookingId);

//        Optional<DriverDetail> optionalDriverDetail  = driverDetailRepository.findDriverByBookingId(Long.valueOf(bookingId));
//        DriverDetail driverDetail = optionalDriverDetail.orElse(null);
        MyBookingDto contract = viewEditBookingService.viewBookingDetailContract(bookingId, carId, session, userId);
        MyBookingDto actual = viewEditBookingService.viewBookingDetailActual(bookingId, carId, session, userId);
        Optional<Feedback> opFeedback = ratingStarRepo.findByBookingId(Long.valueOf(bookingId));
        Feedback feedback = opFeedback.orElse(null);
        boolean isRating = feedback == null && booking.getBookingStatus().equalsIgnoreCase("Completed");

        String lateTime = "";
        LocalDateTime timeNow = LocalDateTime.now();
        if(CalculateNumberOfDays.calculateLateTime(booking.getEndDate(), timeNow) != null) {
            lateTime = CalculateNumberOfDays.calculateLateTime(booking.getEndDate(), timeNow);
        }




        Map<String, Long> map = CalculateNumberOfDays.calculateNumberOfDays(booking.getStartDate(), timeNow);
        Double hourlyRate = car.getBasePrice() / 24;
        Double totalPrice = CalculateNumberOfDays.calculateRentalFee(map,car.getBasePrice(),  hourlyRate);

        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> "Customer".equals(role.getRoleName()));



        //=========================================================== Car detail ===================================================


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
        //=====================================================================================================

        model.addAttribute("users", listDriver);
        model.addAttribute("car", car);
        model.addAttribute("user", user);
        model.addAttribute("rentUser", rentUser);
        model.addAttribute("booking", booking);
        model.addAttribute("navigate", navigate);
        model.addAttribute("feedbackDto", new FeedbackDto());
        model.addAttribute("isRating",isRating); // có hiện đánh giá hay không
        model.addAttribute("lateTime",lateTime);
        model.addAttribute("timNow",timeNow);
        model.addAttribute("totalPrice",totalPrice);
        model.addAttribute("isCustomer", isCustomer);
        model.addAttribute("haveDriver",booking.getDriverId());
        model.addAttribute("contract", contract);
        model.addAttribute("actual", actual);
        return "customer/EditBookingDetails";
    }


    @PostMapping("/update-booking-car")
    @JsonProperty
    @ResponseBody
    public ResponseEntity<?> saveBooking(
            @RequestParam(value = "bookingInfo") String BookingJson,
            @RequestParam(value = "drivingLicense", required = false) MultipartFile rentImage,
            HttpSession session
    ) throws IOException {

        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        BookingDto bookingInfor = objectMapper.readValue(BookingJson, BookingDto.class);

        String fullName = bookingInfor.getRentFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Full Name is required");
        }
        String email = bookingInfor.getRentMail();
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (email.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email");
        }

        String phone = bookingInfor.getRentPhone();
        pattern = Pattern.compile(Regex.PHONE_REGEX);
        matcher = pattern.matcher(phone);
        if (phone.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Phone");
        }
        String nationalId = bookingInfor.getRentNationalId();
        pattern = Pattern.compile(Regex.NATIONAL_ID_REGEX);
        matcher = pattern.matcher(nationalId);
        if (nationalId.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid nationalId");
        }

        LocalDate dob = bookingInfor.getRentBookPickDate();
//        pattern = Pattern.compile(NATIONAL_ID_REGEX);
//        matcher = pattern.matcher(nationalId);
        if (dob == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        LocalDate today = LocalDate.now();
        int age = Period.between(dob, today).getYears();

        if (age < 18 || age > 80) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Age must be between 18 and 80 years.");
        }

        if(user.getDrivingLicense() == null && rentImage == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a driving license image.");
        }


//        LocalDateTime startDate = bookingInfor.getPickUpDate();
//        if (startDate == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Start Date");
//        }
//
//        LocalDateTime endDate = bookingInfor.getReturnDate();
//        if (endDate == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid End Date");
//        }

//        // Kiểm tra endDate có phải sau startDate ít nhất một giờ không
//        if (Duration.between(startDate, endDate).toHours() < 1) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date must be at least 1 hour after start date");
//        }
//
//        if (endDate.isBefore(LocalDateTime.now()) || startDate.isBefore(LocalDateTime.now())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date Time cannot be in the past");
//        }
        try {

            MultipartFile[] files = {rentImage};
            viewEditBookingService.updateBooking(bookingInfor, files, session);
            response.put("message", "update successfully.");
            //response.put("bookingInfo", Map.of("id", booking.getBookingId(), "startDate", booking.getStartDate(), "endDate", booking.getEndDate()));
        } catch (RuntimeException e) {
            switch (e.getMessage()) {
                case "Your wallet must be greater than deposit":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your wallet must be greater than deposit");
                case "Other Pay Method not helps now, please use your wallet":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Other Pay Method not helps now, please use your wallet");
                case "Phone number already exists":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number already exists");
            }
        }

        return ResponseEntity.ok(response);
        
    }


    @GetMapping("/cancel-booking-detail")
    public String cancelBooking(@RequestParam Long bookingId,@RequestParam Integer carId, @RequestParam Integer userId,@RequestParam String navigate,  Model model, HttpSession session) {

        boolean isCancelled = rentalCarService.cancelBooking(bookingId, session);
        if (isCancelled) {
            model.addAttribute("message_" + bookingId, "Booking has been successfully cancelled.");
        } else {
            model.addAttribute("error", "Unable to cancel the booking. Please try again.");
        }

       return bookingDetail(bookingId.intValue(), carId, userId, navigate, model, session);
    }


    @GetMapping("/confirm-pickup-booking-detail")
    public String confirmPickupBooking(@RequestParam Long bookingId,@RequestParam Integer carId,@RequestParam Integer userId,@RequestParam String navigate,  Model model, HttpSession session) {

        boolean isConfirm = rentalCarService.confirmPickupBooking(bookingId, session);

        if (isConfirm) {
            model.addAttribute("message", "Booking has been successfully confirm pick-up.");
        } else {
            model.addAttribute("error", "Unable to confirm the booking. Please try again.");
        }

        return bookingDetail(bookingId.intValue(), carId, userId, navigate, model, session);
    }


    @GetMapping("/api/update-drivers/{id}")
    public ResponseEntity<List<UserDto>> getAllDrivers(@PathVariable Integer id) {
        List<UserDto> drivers = getAllDriverAvailable(id);
        return ResponseEntity.ok(drivers);
    }


    public List<UserDto> getAllDriverAvailable(Integer bookingId) {
        List<Object[]> results = userRepo.getAllDriver(bookingId);
        List<UserDto> userDtos = new ArrayList<>();

        for (Object[] result : results) {
            Long userId = ((Number) result[0]).longValue();
            String fullName = (String) result[1];
            LocalDate dob = null;
            if (result[2] != null) {
                java.sql.Date sqlDate = (java.sql.Date) result[2];
                dob = sqlDate.toLocalDate();
            }
            String phone = (String) result[3];
            UserDto userDto = new UserDto(userId, fullName,null, dob, null, phone, null, null);
            userDtos.add(userDto);
        }

        return userDtos;
    }

    @GetMapping("/confirm-deposit-booking-detail")
    public String confirmDepositBookingDetail(@RequestParam Integer bookingId,
                                      @RequestParam Integer carId,
                                      @RequestParam Integer userId ,
                                      @RequestParam String navigate,
                                      Model model,
                                      HttpSession session) {
        boolean isConfirmDeposit = rentalCarService.confirmDepositCar(Long.valueOf(carId), session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "The Car deposit has been confirmed.");
        } else {
            model.addAttribute("error", "Unable to confirm deposit the booking. Please try again!!");
        }
        return bookingDetail(bookingId.intValue(), carId, userId, navigate, model, session);

    }

    @GetMapping("/confirm-cancel-booking-detail")
    public String confirmCancelBookingDetail(@RequestParam Integer bookingId,
                                      @RequestParam Integer carId,
                                      @RequestParam Integer userId ,
                                      @RequestParam String navigate,
                                      Model model,
                                      HttpSession session) {
        boolean isConfirmDeposit = rentalCarService.confirmCancelBookingCar(Long.valueOf(carId), session);
        if (isConfirmDeposit) {
            model.addAttribute("message_" + carId, "The Car deposit has been confirmed.");
        } else {
            model.addAttribute("error", "Unable to confirm deposit the booking. Please try again!!");
        }
        return bookingDetail(bookingId.intValue(), carId, userId, navigate, model, session);
    }
}
