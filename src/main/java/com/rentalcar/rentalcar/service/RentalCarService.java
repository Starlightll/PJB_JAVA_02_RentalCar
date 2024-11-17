package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.Booking;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface RentalCarService {

    Page<MyBookingDto> getBookings(int page, int limit, String sortBy, String order, HttpSession session);
    boolean cancelBooking(Long bookingId, HttpSession session);
    boolean confirmPickupBooking(Long bookingId, HttpSession session);
    CarDto getCarDetails(Integer carId);
    Booking saveBooking(BookingDto BookingDto, MultipartFile[] files, HttpSession session);
    boolean confirmDepositCar(Long carId, HttpSession session);
    boolean confirmPaymentCar(Long carId, HttpSession session);


}
