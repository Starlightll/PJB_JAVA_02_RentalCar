package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.*;

@Controller
public class RentalCarController {

    @Autowired
    RentalCarService rentalCarService;


    @GetMapping("/my-bookings")
    public String myBooking(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(defaultValue = "lastModified") String sortBy,
                            @RequestParam(defaultValue = "desc") String order,
                            Model model,
                            HttpSession session) {

        Page<BookingDto>  bookingPages = rentalCarService.getBookings(page, size, sortBy, order, session);

        List<BookingDto> bookingList = bookingPages.getContent();
        if (bookingList.isEmpty()) {
            model.addAttribute("message", "You have no booking");
        }else {
            model.addAttribute("bookingList", bookingList);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookingPages.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
            model.addAttribute("totalElement",bookingPages.getTotalElements());
        }
        return "customer/MyBookings";
    }



    //Add
    @GetMapping("/booking-car")
    public String bookingDetail(@RequestParam Integer CarId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        CarDto car = rentalCarService.getCarDetails(CarId);


        model.addAttribute("car", car);
        model.addAttribute("user", user);
        return "customer/booking";
    }

    @PostMapping("/booking-car/data")
    public ResponseEntity<?> saveBooking(
            @RequestParam(value = "booking") String BookingJson,
            @RequestParam(value = "drivingLicense", required = false) MultipartFile rentImage,
            @RequestParam(value = "driverDrivingLicense", required = false) MultipartFile driverImage
            ) throws IOException {


        // Parse carDraft JSON
        ObjectMapper objectMapper = new ObjectMapper();
        BookingDto bookingInfor = objectMapper.readValue(BookingJson, BookingDto.class);
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedBasePrice = df.format(bookingInfor.getBasePrice() == null ? 0 : bookingInfor.getBasePrice());
        String fullName = bookingInfor.getFullname();
        if(fullName == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Full Name is required");
        }
        String email = bookingInfor.getEmail();
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if(email.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email");
        }

        String phone = bookingInfor.getPhone();
        pattern = Pattern.compile(PHONE_REGEX);
        matcher = pattern.matcher(phone);
        if(phone.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Phone");
        }
        String nationalId = bookingInfor.getPhone();
        pattern = Pattern.compile(NATIONAL_ID_REGEX);
        matcher = pattern.matcher(nationalId);
        if(nationalId.isEmpty() || !matcher.matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid nationalId");
        }

        LocalDate dob = bookingInfor.getDob();
//        pattern = Pattern.compile(NATIONAL_ID_REGEX);
//        matcher = pattern.matcher(nationalId);
        if(dob == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Date");
        }
        LocalDate today = LocalDate.now();
        int age = Period.between(dob, today).getYears();

        if (age < 18) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 18 years old");
        }


        LocalDateTime startDate = bookingInfor.getStartDate();
        if(startDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Start Date");
        }

        LocalDateTime endDate = bookingInfor.getEndDate();
        if(endDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid End Date");
        }





        // Nếu tất cả dữ liệu hợp lệ
        return ResponseEntity.ok("Step 1 validated successfully.");
    }



}
