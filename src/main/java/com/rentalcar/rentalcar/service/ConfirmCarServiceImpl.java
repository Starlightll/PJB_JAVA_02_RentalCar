package com.rentalcar.rentalcar.service;

import jakarta.servlet.http.HttpSession;

public class ConfirmCarServiceImpl implements ConfirmCarService {
    @Override
    public boolean confirmDepositCar(Long carId, HttpSession session) {
        return false;
    }

    @Override
    public boolean confirmPaymentCar(Long carId, HttpSession session) {
        return false;
    }
}
