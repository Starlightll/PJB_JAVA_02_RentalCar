package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
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

import java.math.BigDecimal;
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

    @Autowired PhoneNumberStandardService phoneNumberStandardService;


    @Override
    public void registerUser(RegisterDto userDto) throws IllegalArgumentException {
        if(userRepository.getUserByEmail(userDto.getEmail()) != null) {
            throw new UserException("Email already exists");
        }

        if (!userDto.getPassword().equalsIgnoreCase(userDto.getConfirmPassword())) {
            throw new UserException("Passwords do not match");
        }

        if(phoneNumberStandardService.isPhoneNumberExists(userDto.getPhoneNumber(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE)) {
            throw new IllegalArgumentException("Phone number already exists");
        }



        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        String normalizedPhone = phoneNumberStandardService.normalizePhoneNumber(userDto.getPhoneNumber(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);


        // Save user in the database
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(encodedPassword);
        user.setPhone(normalizedPhone);
        user.setAgreeTerms(true);
        user.setUsername(userDto.getUsername());
        user.setStatus(UserStatus.PENDING);
        user.setWallet(BigDecimal.valueOf(0));
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

        if(user.getStatus().equals(UserStatus.LOCKED)) {
            return "Account Locked";
        }
        if (user.getStatus().equals(UserStatus.ACTIVATED)) {
            return "Account activated.";
        }

        // Activate user
        user.setStatus(UserStatus.ACTIVATED);
        userRepository.save(user);

        return "Token valid";
    }


    @Override
    public void resendActivationToken(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new UserException("User not found.");
        }


        if(user.getStatus().equals(UserStatus.LOCKED)) {
            throw new UserException("Account Locked");
        }

        if (user.getStatus().equals(UserStatus.ACTIVATED)) {
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
