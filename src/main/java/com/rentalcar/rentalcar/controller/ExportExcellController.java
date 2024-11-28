package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.helper.ExcelExporter;
import com.rentalcar.rentalcar.service.ViewEditBookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExportExcellController {

    @Autowired
    private ViewEditBookingService viewEditBookingService;

    @GetMapping("/bills")
    public String getBills(@RequestParam(required = false) Integer bookingId,
                           @RequestParam(required = false) Integer carId,
                           HttpSession session,
                           @RequestParam(required = false) Integer userId,
                           Model model) {
        // Dummy data
        MyBookingDto bills = viewEditBookingService.getBookingDetail(bookingId, carId, session, userId);

        // Nếu cần, có thể lọc danh sách hóa đơn theo startDate và endDate
        model.addAttribute("bills", bills);
        return "customer/test";
    }

    @GetMapping("/bills/export-excel")
    public ResponseEntity<byte[]> exportBillsToExcel(@RequestParam(required = false) Integer bookingId,
                                                     @RequestParam(required = false) Integer carId,
                                                     HttpSession session,
                                                     @RequestParam(required = false) Integer userId,
                                                     Model model) {
        try {
            // Dummy data
            MyBookingDto bill = viewEditBookingService.getBookingDetail(bookingId, carId, session, userId);
            return ExcelExporter.exportBillsToExcel(bill);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
