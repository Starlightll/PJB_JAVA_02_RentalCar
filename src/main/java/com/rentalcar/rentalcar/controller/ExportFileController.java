package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.helper.FileExporter;
import com.rentalcar.rentalcar.service.ViewEditBookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class ExportFileController {

    @Autowired
    private ViewEditBookingService viewEditBookingService;

    @GetMapping("/view-booking-detail")
    public String getBills(@RequestParam(required = false) Integer bookingId,
                           @RequestParam(required = false) Integer carId,
                           HttpSession session,
                           @RequestParam(required = false) Integer userId,
                           Model model) {
        // Dummy data
        User user = (User) session.getAttribute("user");
        LocalDateTime timeNow = LocalDateTime.now();
        MyBookingDto contract = viewEditBookingService.viewBookingDetailContract(bookingId, carId, session, userId);
        MyBookingDto actual = viewEditBookingService.viewBookingDetailActual(bookingId, carId, session, userId);
        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> "Customer".equals(role.getRoleName()));
        // Nếu cần, có thể lọc danh sách hóa đơn theo startDate và endDate
        model.addAttribute("contract", contract);
        model.addAttribute("actual", actual);
        model.addAttribute("timeNow", timeNow);
        model.addAttribute("isCustomer", isCustomer);

        return "customer/viewBookingDetail";
    }

    @GetMapping("/bills/export-pdf")
    public ResponseEntity<byte[]> exportBillsToExcel(@RequestParam(required = false) Integer bookingId,
                                                     @RequestParam(required = false) Integer carId,
                                                     HttpSession session,
                                                     @RequestParam(required = false) Integer userId,
                                                     Model model) {
        try {
            // Dummy data
            MyBookingDto contract = viewEditBookingService.viewBookingDetailContract(bookingId, carId, session, userId);
            MyBookingDto actual = viewEditBookingService.viewBookingDetailActual(bookingId, carId, session, userId);
            return FileExporter.exportBillToPdf(contract, actual);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
