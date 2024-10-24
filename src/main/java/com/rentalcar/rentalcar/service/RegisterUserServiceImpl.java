package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.UserRole;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.repository.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterUserServiceImpl implements RegisterUserService {
    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRoleRepo userRoleRepo;


    @Override
    public void registerUser(RegisterDto userDto) {
        if(userRepository.getUserByEmail(userDto.getEmail()) != null) {
            throw new UserException("Email already exists");
        }

        if (!userDto.getPassword().equalsIgnoreCase(userDto.getConfirmPassword())) {
            throw new UserException("Passwords do not match");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // Save user in the database
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(encodedPassword);
        user.setPhone(userDto.getPhoneNumber());
        user.setEnabled(false);
        user.setUsername(userDto.getUsername());
        userRepository.save(user);


        // Save userRole in the database
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(userDto.getRole());
        userRoleRepo.save(userRole);



        // Create verification token
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(user, token);

        // Send confirmation email
        emailService.sendVerificationEmail(user, token);
    }

    @Override
    public String confirmToken(String token) {
        VerificationToken verificationToken = tokenService.getVerificationToken(token);
        if (verificationToken == null) {
            return "Invalid token";
        }

        // Check token expiration
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired";
        }


        User user = verificationToken.getUser();

        if (user.isEnabled()) {
            return "Account activated.";
        }
        // Activate user
        user.setEnabled(true);
        userRepository.save(user);

        return "Token valid";
    }

    @Override
    public void resendActivationToken(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new UserException("User not found.");
        }

        if (user.isEnabled()) {
            throw new UserException("Account is already activated.");
        }

        // Create verification new token
        String newToken = UUID.randomUUID().toString();
        try {
            tokenService.updateVerificationToken( user,newToken );

        } catch (Exception e) {
            throw new UserException("Error occurred while sending activation token: " + e.getMessage());
        }
        emailService.sendVerificationEmail(user, newToken);
    }
}