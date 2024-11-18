package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rentalcar.rentalcar.common.Regex;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.RentalCarService;
import com.rentalcar.rentalcar.service.ReturnCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.rentalcar.rentalcar.common.Regex.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @GetMapping("/my-bookings")
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
    @GetMapping("/booking-car")

    public String bookingDetail(@RequestParam Integer CarId,
                                @RequestParam String startDate, @RequestParam String enDate
                                 , @RequestParam String address, @RequestParam String beforeNavigate, Model model, HttpSession session) {

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

        List<UserDto> driverList = getAllDriverAvailable();
        // Định dạng ngày giờ đầu vào và đầu ra
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        startDate = startDate.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})", "$1/$2/$3");;
        enDate = enDate.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})", "$1/$2/$3");;


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
        model.addAttribute("userRepo", userepo);//Lấy wallet
        model.addAttribute("carAddress", carAddress);//Lấy địa chỉ
        return "customer/booking";
    }

    public List<UserDto> getAllDriverAvailable() {
        List<Object[]> results = userRepo.getAllDriverAvailable();
        List<UserDto> userDtos = new ArrayList<>();

        for (Object[] result : results) {
            Long userId = ((Number) result[0]).longValue();
            String fullName = (String) result[1];
            LocalDate dob = null;
            if (result[2] != null) {
                java.sql.Date sqlDate = (java.sql.Date) result[2];
                dob = sqlDate.toLocalDate();
            }

            UserDto userDto = new UserDto(userId, fullName, null, dob, null, null, null, null);
            userDtos.add(userDto);
        }

        return userDtos;
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
        if (fullName == null) {
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

        if (age < 18) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old");
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
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your wallet must be greater than deposit");
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
        if (isCancelled) {
            model.addAttribute("message_" + bookingId, "Booking has been successfully cancelled.");
        } else {
            model.addAttribute("error", "Unable to cancel the booking. Please try again.");
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
        return "customer/MyBookings";

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

        if (isConfirm) {
            model.addAttribute("message", "Booking has been successfully confirm pick-up.");
        } else {
            model.addAttribute("error", "Unable to confirm the booking. Please try again.");
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
        return "customer/MyBookings";
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
            return generateResponse(response, "success1", "Booking has been successfully completed.");
        } else if (caseReturn == 2) {
            return generateResponse(response, "success2", "Car return request sent. Waiting for Car-Owner to confirm payment.");

        } else if (caseReturn == -1) {
            return generateResponse(response, "error", "Your wallet doesn’t have enough balance. Please top-up your wallet and try again");
        } else if (caseReturn == -2) {
            boolean isUpdate = returnCarService.updateBookingPendingPayment(bookingId, session);
            return generateResponse(response, "error", "Car-owner doesn’t have enough balance. Please try again later!");
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
