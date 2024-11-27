package com.rentalcar.rentalcar.service;

import jakarta.servlet.http.HttpSession;

public interface ReturnCarService {
    String returnCar(Long bookingId, HttpSession session);
    Double calculateTotalPrice(Long bookingId);
    int confirmReturnCar(Long bookingId, HttpSession session);
    boolean updateBookingPendingPayment(Long bookingId, HttpSession session);
    Double calculateTotalPriceForActualEnddateCarOwner(Long bookingId);

    String checkPayment(Long bookingId, HttpSession session);
    int confirmPayment(Long bookingId, HttpSession session);

}
