package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Booking;

public interface BookingService {

    Boolean checkAlreadyBookedCar(Integer carId, Long userId);
    Boolean checkOverdueBooking(Long bookingId);

    void cancelAllWaitingBookingOfCarOfUser(Long bookingId, Integer carId, Long userId);

    Integer cancelExpiredBooking();

    void cancelBooking(Booking booking);
    void confirmBooking(Booking booking);

}
