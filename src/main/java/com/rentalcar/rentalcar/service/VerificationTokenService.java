package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;

public interface VerificationTokenService {
    public VerificationToken createVerificationToken(User user, String token);

    public void updateVerificationToken(User user, String newToken);

    public VerificationToken getVerificationToken(String token);
}
