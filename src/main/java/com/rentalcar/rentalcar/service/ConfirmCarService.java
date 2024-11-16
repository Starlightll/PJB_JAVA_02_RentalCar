package com.rentalcar.rentalcar.service;

import jakarta.servlet.http.HttpSession;

public interface ConfirmCarService {
    boolean confirmDepositCar(Long carId, HttpSession session);
    boolean confirmPaymentCar(Long carId, HttpSession session);

}
