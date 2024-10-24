package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordServicImpl implements ForgotPasswordService{

    @Autowired private VerificationTokenService tokenService;

    @Autowired EmailService emailService;

    @Autowired UserDetailsService userDetailsService;

    @Autowired
    private UserRepo userRepository;

    @Override
    public void forgotPassword(String email) throws UserException {
        User user = userDetailsService.loadUserByEmail(email);

        if(!user.isEnabled()) {
            throw new UserException("Account not activated");
        }

        // Create token reset
        String token = UUID.randomUUID().toString();
        tokenService.updateResetPasswordToken(user, token);

        // Send confirmation email
        emailService.sendResetPassword(user, token);
    }

    @Override
    public String checToken(String token) {
        VerificationToken verificationToken = tokenService.getVerificationToken(token);
        if (verificationToken == null) {
            return "Invalid token";
        }

        // Check token expiration
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired";
        }


        return "Token valid";
    }

    @Override
    public void resetPassword(String email, String password, String confirmPassword) throws UserException {
        if(!password.equalsIgnoreCase(confirmPassword)) {
            throw new UserException("Passwords do not match");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        User user = userDetailsService.loadUserByEmail(email);

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
