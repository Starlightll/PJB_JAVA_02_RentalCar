package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.dto.FeedbackDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RentalCarService {

    Page<BookingDto> getBookings(int page, int limit, String sortBy, String order, HttpSession session);
    boolean cancelBooking(Long bookingId, HttpSession session);
    boolean confirmPickupBooking(Long bookingId, HttpSession session);
    CarDto getCarDetails(Integer carId);
}
