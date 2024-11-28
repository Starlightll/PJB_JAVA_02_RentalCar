package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

public interface ViewEditBookingService {
    MyBookingDto getBookingDetail( Integer bookingId, Integer carId, HttpSession session, Integer userId);
    void updateBooking(BookingDto bookingInfor, MultipartFile[] files,HttpSession session);
}
