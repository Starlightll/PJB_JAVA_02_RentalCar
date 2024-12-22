package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Boolean checkAlreadyBookedCar(Integer carId, Long userId) {
        Booking booking = bookingRepository.findBookingByCar_CarIdAndUser_IdAndBookingStatus_BookingStatusId(carId, userId, 1L);
        return booking != null;
    }
}
