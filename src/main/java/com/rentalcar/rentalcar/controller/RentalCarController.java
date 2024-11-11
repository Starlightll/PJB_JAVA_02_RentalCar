package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.service.RentalCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/booking-car/step1")
    public ResponseEntity<?> validateStep1(@RequestParam Map<String, String> formData,
                                           @RequestParam("drivingLicense") MultipartFile drivingLicense,
                                           @RequestParam("carId") Integer carId) {
        // Kiểm tra các trường dữ liệu bắt buộc
        if (formData.get("fullName") == null || formData.get("fullName").isEmpty()) {
            return ResponseEntity.badRequest().body("Full Name is required.");
        }
        if (formData.get("bookPickDate") == null || formData.get("bookPickDate").isEmpty()) {
            return ResponseEntity.badRequest().body("Booking Date is required.");
        }
        if (formData.get("phone") == null || formData.get("phone").isEmpty()) {
            return ResponseEntity.badRequest().body("Phone is required.");
        }
        if (formData.get("email") == null || formData.get("email").isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        if (formData.get("nationalId") == null || formData.get("nationalId").isEmpty()) {
            return ResponseEntity.badRequest().body("National ID is required.");
        }


        // Kiểm tra các trường khác...

        // Xử lý file nếu có
        if (drivingLicense != null && !drivingLicense.isEmpty()) {
            // Xử lý file driving license
        }

        // Nếu tất cả dữ liệu hợp lệ
        return ResponseEntity.ok("Step 1 validated successfully.");
    }



}
