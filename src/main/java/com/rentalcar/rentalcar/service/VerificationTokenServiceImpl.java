package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.repository.VerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    @Autowired
    private VerificationTokenRepo tokenRepository;


    @Override
    public VerificationToken createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        return tokenRepository.save(verificationToken);
    }

    @Override
    public void updateVerificationToken(User user, String newToken) {
        VerificationToken existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            existingToken.setToken(newToken);
            existingToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
            tokenRepository.save(existingToken);
        }
    }

    @Override
    public void updateResetPasswordToken(User user, String newToken) {
        VerificationToken existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            existingToken.setToken(newToken);
            existingToken.setExpiryDate(LocalDateTime.now().plusMinutes(1));
            tokenRepository.save(existingToken);  // Lưu lại token mới
        }
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
