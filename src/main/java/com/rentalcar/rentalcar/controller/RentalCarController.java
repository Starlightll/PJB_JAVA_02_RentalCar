package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.RentalCarRepository;
import com.rentalcar.rentalcar.service.RentalCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
        }
        return "customer/MyBookings";
    }

    @GetMapping("/booking-car")
    public String bookingDetail() {
        return "customer/booking";
    }

}
