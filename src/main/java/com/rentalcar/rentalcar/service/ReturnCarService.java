package com.rentalcar.rentalcar.service;

import jakarta.servlet.http.HttpSession;

public interface ReturnCarService {
    String returnCar(Long bookingId, HttpSession session);
    Double calculateTotalPrice(Long bookingId);
}
