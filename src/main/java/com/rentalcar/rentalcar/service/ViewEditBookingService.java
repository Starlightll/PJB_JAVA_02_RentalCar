package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import jakarta.servlet.http.HttpSession;

public interface ViewEditBookingService {
    MyBookingDto getBookingDetail( Integer bookingId, Integer carId, HttpSession session);
}
