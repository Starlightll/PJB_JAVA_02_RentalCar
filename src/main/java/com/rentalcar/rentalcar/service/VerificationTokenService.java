package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;

public interface VerificationTokenService {
     VerificationToken createVerificationToken(User user, String token);

     void updateVerificationToken(User user, String newToken);

     void updateResetPasswordToken(User user, String newToken);

     VerificationToken getVerificationToken(String token);
}
