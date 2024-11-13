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
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.RentalCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import static com.rentalcar.rentalcar.common.Regex.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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

    @GetMapping("/my-bookings")
    public String myBooking(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
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


        Page<MyBookingDto>  bookingPages = rentalCarService.getBookings(page, size, sortBy, order, session);

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
        }else {
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

    public String bookingDetail(@RequestParam Integer CarId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        CarDto car = rentalCarService.getCarDetails(CarId);

        List<UserDto> users = getAllDriverAvailable();
        model.addAttribute("users", users);
        model.addAttribute("car", car);
        model.addAttribute("user", user);
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

            UserDto userDto = new UserDto(userId, fullName, dob);
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

        if(bookingInfor.getStep() == 2) {
            response.put("message", "Step 1 validated successfully.");
            return ResponseEntity.ok(response);
        } else {
            MultipartFile[] files = {rentImage, driverImage};
            try {
              Booking booking =  rentalCarService.saveBooking(bookingInfor, files, session);
                response.put("message", "Step 2 validated successfully.");
                response.put("bookingInfo", Map.of("id",booking.getBookingId(),"startDate", booking.getStartDate(), "endDate", booking.getEndDate()));
            }catch (RuntimeException e) {
                switch (e.getMessage()) {
                    case "Your wallet must be greater than deposit":
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your wallet must be greater than deposit");
                    case "Other Pay Method not helps now, please use your wallet":
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Other Pay Method not helps now, please use your wallet");

                }
            }

            return ResponseEntity.ok(response);
        }

    }




        @GetMapping("/cancel-booking")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId, HttpSession session, Model model) {
        boolean isCancelled = rentalCarService.cancelBooking(bookingId, session);

        if (isCancelled) {
            model.addAttribute("message", "Booking has been successfully cancelled.");
        } else {
            model.addAttribute("error", "Unable to cancel the booking. Please try again.");
        }

        return "redirect:/my-bookings";

    }

    @GetMapping("/confirm-pickup-booking")
    public String confirmPickupBooking(@RequestParam("bookingId") Long bookingId, HttpSession session, Model model) {
        boolean isConfirm = rentalCarService.confirmPickupBooking(bookingId, session);

        if (isConfirm) {
            model.addAttribute("message", "Booking has been successfully confirm pick-up.");
        } else {
            model.addAttribute("error", "Unable to confirm the booking. Please try again.");
        }

        return "redirect:/my-bookings";
    }

}
