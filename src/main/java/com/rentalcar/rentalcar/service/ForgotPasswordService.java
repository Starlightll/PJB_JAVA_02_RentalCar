package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;

public interface ForgotPasswordService {
    void forgotPassword(String email);

    String checToken(String token);

    void resetPassword(String email, String password, String confirmPassword);
}
