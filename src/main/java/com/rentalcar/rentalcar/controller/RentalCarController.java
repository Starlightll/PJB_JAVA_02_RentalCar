package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rentalcar.rentalcar.common.CalculateNumberOfDays;
import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.Regex;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.DriverDetail;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.PhoneNumberStandardService;
import com.rentalcar.rentalcar.service.RentalCarService;
import com.rentalcar.rentalcar.service.ReturnCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Driver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.EMAIL_REGEX;

@Controller
public class RentalCarController {

    @Autowired
    RentalCarService rentalCarService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ReturnCarService returnCarService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    UserRepo userRepository;

    @Autowired
    PhoneNumberStandardService phoneNumberStandardService;

    @Autowired
    EmailService emailService;

    @Autowired
    BookingRepository bookingRepository;

    @GetMapping({"/customer/my-bookings", "/car-owner/my-bookings"})
    public String myBooking(@RequestParam(defaultValue = "1") int page,
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

            default:
                break;
        }


        Page<MyBookingDto> bookingPages = rentalCarService.getBookings(page, size, sortBy, order, session);
        LocalDateTime timeNow = LocalDateTime.now();
        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> "Customer".equals(role.getRoleName()));
        List<MyBookingDto> bookingList = bookingPages.getContent();
        //check so on-going bookings
        long onGoingBookings = bookingList.stream()
                .filter(booking ->
                        booking.getBookingStatus().equals("In-Progress") ||
                                booking.getBookingStatus().equals("Pending payment") ||
                                booking.getBookingStatus().equals("Pending deposit") ||
                                booking.getBookingStatus().equals("Confirmed"))
                .count();
        model.addAttribute("onGoingBookings", onGoingBookings);
        model.addAttribute("isCustomer", isCustomer);
        model.addAttribute("user", user);
        model.addAttribute("timeNow", timeNow);
        if (bookingList.isEmpty()) {
            model.addAttribute("message", "You have no booking");
        } else {
            model.addAttribute("bookingList", bookingList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookingPages.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
            model.addAttribute("totalElement", bookingPages.getTotalElements());
        }
        return "customer/MyBookings";
    }


    //Add
    @GetMapping("/customer/booking-car")

    public String bookingDetail(@RequestParam Integer CarId,
                                @RequestParam(value = "startDate", required = false) String startDate,
                                @RequestParam(value = "enDate", required = false) String enDate,
                                @RequestParam(value = "address", required = false) String address,
                                @RequestParam(value = "beforeNavigate", required = false) String beforeNavigate,
                                Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        CarDto car = rentalCarService.getCarDetails(CarId);
        User userepo = userRepo.getUserById(user.getId());

        if(car.getStatusId() != 1) {
            return "redirect:/";
        }

        Car carAddress = carRepository.getCarByCarId(CarId);
        if(carAddress == null) {
            return "redirect:/";
        }

        Long numberOfRide = bookingRepository.countCompletedBookingsByCarId(CarId);

        List<UserDto> driverList = getAllDriverAvailable(carAddress.getAddress().getProvinceId());
        // Định dạng ngày giờ đầu vào và đầu ra
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        startDate = startDate.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})", "$1/$2/$3");
        enDate = enDate.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})", "$1/$2/$3");


        SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeOutputFormat = new SimpleDateFormat("HH:mm");
        try {


            // Chuyển đổi startDate và enDate sang định dạng mong muốn
            Date start = inputFormat.parse(startDate);
            Date end = inputFormat.parse(enDate);


            // Định dạng ngày và giờ riêng biệt
            String pickStartDate = dateOutputFormat.format(start); // Ngày bắt đầu
            String pickTime = timeOutputFormat.format(start); // Giờ bắt đầu

            String dropDate = dateOutputFormat.format(end); // Ngày kết thúc
            String dropTime = timeOutputFormat.format(end); // Giờ kết thúc



            model.addAttribute("pickDate", pickStartDate);
            model.addAttribute("dropDate", dropDate);
            model.addAttribute("pickTime", pickTime);
            model.addAttribute("dropTime", dropTime);

        } catch (ParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu không thể phân tích chuỗi đầu vào
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("enDate", enDate);
        model.addAttribute("lastLink", beforeNavigate);
        model.addAttribute("users", driverList); //
        model.addAttribute("car", car);
        model.addAttribute("address", address);
        model.addAttribute("user", user);
        model.addAttribute("numberOfRide", numberOfRide);
        model.addAttribute("userRepo", userepo);//Lấy wallet
        model.addAttribute("carAddress", carAddress);//Lấy địa chỉ
        return "customer/booking";
    }

    @GetMapping("/customer/booking-car-v2")
    public String bookingDetailV2(@RequestParam(value = "CarId") Integer CarId,
                                @RequestParam(value = "startDate", required = false) String startDate,
                                @RequestParam(value = "endDate", required = false) String endDate,
                                @RequestParam(value = "address", required = false) String address,
                                @RequestParam(value = "beforeNavigate", required = false) String beforeNavigate,
                                Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        CarDto car = rentalCarService.getCarDetails(CarId);
        User userepo = userRepo.getUserById(user.getId());

        if (car.getStatusId() != 1) {
            return "redirect:/";
        }

        Car carAddress = carRepository.getCarByCarId(CarId);
        if (carAddress == null) {
            return "redirect:/";
        }

        List<UserDto> driverList = getAllDriverAvailable(carAddress.getAddress().getProvinceId());

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm");
        DateTimeFormatter dateOutputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeOutputFormatter = DateTimeFormatter.ofPattern("HH:mm");

        try {
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = LocalDateTime.parse(startDate, inputFormatter);
                LocalDateTime endDateTime = LocalDateTime.parse(endDate, inputFormatter);

                String formattedStartDate = startDateTime.format(outputFormatter);
                String formattedEnDate = endDateTime.format(outputFormatter);

                String pickStartDate = startDateTime.format(dateOutputFormatter);
                String pickTime = startDateTime.format(timeOutputFormatter);

                String dropDate = endDateTime.format(dateOutputFormatter);
                String dropTime = endDateTime.format(timeOutputFormatter);

                model.addAttribute("startDate", formattedStartDate);
                model.addAttribute("enDate", formattedEnDate);
                model.addAttribute("pickDate", pickStartDate);
                model.addAttribute("dropDate", dropDate);
                model.addAttribute("pickTime", pickTime);
                model.addAttribute("dropTime", dropTime);
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Invalid date format. Please use 'yyyy-MM-dd HH:mm'.");
            return "redirect:/";
        }

        model.addAttribute("lastLink", beforeNavigate);
        model.addAttribute("users", driverList);
        model.addAttribute("car", car);
        model.addAttribute("address", address);
        model.addAttribute("user", user);
        model.addAttribute("userRepo", userepo);
        model.addAttribute("carAddress", carAddress);

        return "customer/booking";
    }

    public List<UserDto> getAllDriverAvailable(Integer idLocation) {
        List<Object[]> results = userRepo.getAllDriverAvailable(idLocation);
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
            UserDto userDto = new UserDto(userId, fullName, null, null ,dob, null, phone, null, null);
            userDtos.add(userDto);
        }

        return userDtos;
    }


//    @GetMapping("/api/drivers")
//    public ResponseEntity<List<UserDto>> getAllDrivers() {
//        List<UserDto> drivers = getAllDriverAvailable();
//        return ResponseEntity.ok(drivers);
//    }

    @GetMapping("/api/drivers/{id}")
    public ResponseEntity<User> getDriverById(@PathVariable Long id) {
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping(value = "/booking-car")
    @JsonProperty
    @ResponseBody
    public ResponseEntity<?> saveBooking(
            @RequestParam(value = "booking") String BookingJson,
            @RequestParam(value = "drivingLicense", required = false) MultipartFile rentImage,
            @RequestParam(value = "driverDrivingLicense", required = false) MultipartFile driverImage,
            HttpSession session
    ) throws IOException {


        // Parse carDraft JSON
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("user");
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

        User customer = userRepository.getUserById(user.getId());
        String normalizedPhone = phoneNumberStandardService.normalizePhoneNumber(phone, Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);


        if(!Objects.equals(normalizedPhone, customer.getPhone()) && phoneNumberStandardService.isPhoneNumberExists(phone, Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number already exists");
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


        LocalDateTime startDate = bookingInfor.getPickUpDate();
        if (startDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Start Date");
        }

        LocalDateTime endDate = bookingInfor.getReturnDate();
        if (endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid End Date");
        }

        // Kiểm tra endDate có phải sau startDate ít nhất một giờ không
        if (Duration.between(startDate, endDate).toHours() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date must be at least 1 hour after start date");
        }

        if (endDate.isBefore(LocalDateTime.now()) || startDate.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date Time cannot be in the past");
        }
        if(user.getDrivingLicense() == null && rentImage == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a driving license image.");
        }


        if (bookingInfor.getStep() == 2) {
            response.put("message", "Step 1 validated successfully.");
            return ResponseEntity.ok(response);
        } else {
            MultipartFile[] files = {rentImage, driverImage};
            try {
                Booking booking = rentalCarService.saveBooking(bookingInfor, files, session);
                response.put("message", "Step 2 validated successfully.");
                response.put("bookingInfo", Map.of("id", booking.getBookingId(), "startDate", booking.getStartDate(), "endDate", booking.getEndDate()));
            } catch (RuntimeException e) {
                switch (e.getMessage()) {
                    case "Your wallet must be greater than deposit":
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your current balance is insufficient. Please top up or choose another payment method");
                    case "Other Pay Method not helps now, please use your wallet":
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Other Pay Method not helps now, please use your wallet");
                    case "Email already exists" :
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
                    case "Phone number already exists":
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number already exists");
                }
            }

            return ResponseEntity.ok(response);
        }

    }


    @GetMapping("/cancel-booking")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId,
                                HttpSession session,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "lastModified") String sortBy,
                                @RequestParam(defaultValue = "desc") String order,
                                Model model) {
        boolean isCancelled = rentalCarService.cancelBooking(bookingId, session);
        User user = (User) session.getAttribute("user");

        if (isCancelled) {
            model.addAttribute("message_" + bookingId, "Waiting CarOwner confirm cancel this booking!");
        } else {
            model.addAttribute("error", "Unable to cancel the booking. Please try again.");
        }

        return myBooking(page, size, sortBy, order, model, session);


    }


    @GetMapping("/confirm-pickup-booking")
    public String confirmPickupBooking(@RequestParam("bookingId") Long bookingId,
                                       HttpSession session,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "lastModified") String sortBy,
                                       @RequestParam(defaultValue = "desc") String order,
                                       Model model) {
        boolean isConfirm = rentalCarService.confirmPickupBooking(bookingId, session);
        User user = (User) session.getAttribute("user");


        if (isConfirm) {
            model.addAttribute("message", "Booking has been successfully confirm pick-up.");
        } else {
            model.addAttribute("error", "Unable to confirm the booking. Please try again.");
        }

        return myBooking(page, size, sortBy, order, model, session);
    }

    @GetMapping("/return-car")
    public ResponseEntity<?> returnCar(@RequestParam("bookingId") Long bookingId,
                                       HttpSession session,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "lastModified") String sortBy,
                                       @RequestParam(defaultValue = "desc") String order) {
        String responseMessage = returnCarService.returnCar(bookingId, session);

        // Trả về thông điệp JSON với trạng thái "success"
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", responseMessage);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/check-payment")
    public ResponseEntity<?> checkPayment(@RequestParam("bookingId") Long bookingId,
                                       HttpSession session,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "lastModified") String sortBy,
                                       @RequestParam(defaultValue = "desc") String order) {
        String responseMessage = returnCarService.checkPayment(bookingId, session);

        // Trả về thông điệp JSON với trạng thái "success"
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", responseMessage);
        return ResponseEntity.ok(response);

    }



    @GetMapping("/confirm-return-car")
    public ResponseEntity<Map<String, Object>> confirmReturnCar(@RequestParam("bookingId") Long bookingId,
                                                                HttpSession session,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                @RequestParam(defaultValue = "lastModified") String sortBy,
                                                                @RequestParam(defaultValue = "desc") String order,
                                                                Model model) {
        // Retrieve user from session
        User user = (User) session.getAttribute("user");

        // Initialize response map
        Map<String, Object> response = new HashMap<>();

        // Check if the user is present in the session
        if (user == null) {
            return generateResponse(response, "error", "User not found in session.");
        }

        // Call confirmReturnCar to process the return logic
        int caseReturn = returnCarService.confirmReturnCar(bookingId, session);

        // Handle response based on caseReturn value
        if (caseReturn == 1 ) {
            return generateResponse(response, "success1", "Car return request sent successfully!!");
        } else if (caseReturn == 2) {
            return generateResponse(response, "success2", "Car return request sent. Waiting for Car-Owner to confirm.");

        } else if (caseReturn == -1) {
            return generateResponse(response, "error", "Your wallet does’t have enough balance to pay driver salary. Please top-up your wallet and try again");
        }

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

            default:
                break;
        }
        Page<MyBookingDto> bookingPages = rentalCarService.getBookings(page, size, sortBy, order, session);
        List<MyBookingDto> bookingList = bookingPages.getContent();
        long onGoingBookings = bookingList.stream()
                .filter(booking ->
                        booking.getBookingStatus().equals("In-Progress") ||
                                booking.getBookingStatus().equals("Pending payment") ||
                                booking.getBookingStatus().equals("Pending deposit") ||
                                booking.getBookingStatus().equals("Confirmed"))
                .count();
        model.addAttribute("onGoingBookings", onGoingBookings);

        if (bookingList.isEmpty()) {
            model.addAttribute("message", "You have no booking");
        } else {
            model.addAttribute("bookingList", bookingList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookingPages.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
            model.addAttribute("totalElement", bookingPages.getTotalElements());

        }

        return generateResponse(response, "error", "Error Return!");
    }

    @GetMapping("/confirm-payment-car")
    public ResponseEntity<Map<String, Object>> confirmPaymentCar(@RequestParam("bookingId") Long bookingId,
                                                                HttpSession session,
                                                                @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                @RequestParam(defaultValue = "lastModified") String sortBy,
                                                                @RequestParam(defaultValue = "desc") String order,
                                                                Model model) {
        // Retrieve user from session
        User user = (User) session.getAttribute("user");

        // Initialize response map
        Map<String, Object> response = new HashMap<>();

        // Check if the user is present in the session
        if (user == null) {
            return generateResponse(response, "error", "User not found in session.");
        }

        // Call confirmReturnCar to process the return logic
        int caseReturn = returnCarService.confirmPayment(bookingId, session);

        // Handle response based on caseReturn value
        if (caseReturn == 1 ) {
            return generateResponse(response, "success1", "Payment successfully. Booking successfully completed!");
        }  else if (caseReturn == -1) {
            return generateResponse(response, "error", "Your wallet does’t have enough balance to pay driver salary. Please top-up your wallet and try again");
        }

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

            default:
                break;
        }
        Page<MyBookingDto> bookingPages = rentalCarService.getBookings(page, size, sortBy, order, session);
        List<MyBookingDto> bookingList = bookingPages.getContent();
        long onGoingBookings = bookingList.stream()
                .filter(booking ->
                        booking.getBookingStatus().equals("In-Progress") ||
                                booking.getBookingStatus().equals("Pending payment") ||
                                booking.getBookingStatus().equals("Pending deposit") ||
                                booking.getBookingStatus().equals("Confirmed"))
                .count();
        model.addAttribute("onGoingBookings", onGoingBookings);

        if (bookingList.isEmpty()) {
            model.addAttribute("message", "You have no booking");
        } else {
            model.addAttribute("bookingList", bookingList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookingPages.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
            model.addAttribute("totalElement", bookingPages.getTotalElements());

        }

        return generateResponse(response, "error", "Error Return!");
    }

    private ResponseEntity<Map<String, Object>> generateResponse(Map<String, Object> response, String status, String message) {
        response.put("status", status);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}
