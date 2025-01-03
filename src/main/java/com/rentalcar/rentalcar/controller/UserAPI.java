package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.PhoneNumberStandardService;
import com.rentalcar.rentalcar.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * REST controller for managing user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    PhoneNumberStandardService phoneNumberStandardService;

    @GetMapping("/")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean exists = userService.checkEmail(email);
        return ResponseEntity.ok(!exists);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhone(
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        if(userId != null) {
            User user = userRepo.getUserById(Long.parseLong(userId));
            String phoneNormalized = phoneNumberStandardService.normalizePhoneNumber(phone, Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);
            if(user.getPhone().equals(phoneNormalized)) {
                return ResponseEntity.ok(true);
            }
        }
        phone = phone.replaceAll("[^0-9]", "");
        boolean exists = phoneNumberStandardService.isPhoneNumberExists(phone, Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);
        return ResponseEntity.ok(!exists);
    }

    @GetMapping("/check-nationalId")
    public ResponseEntity<?> checkNationalId(
            @RequestParam(value = "nationalId") String nationalId,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        if(userId != null) {
            User user = userRepo.getUserById(Long.parseLong(userId));
            if(user.getNationalId().equals(nationalId)) {
                return ResponseEntity.ok(true);
            }
        }
        boolean exists = userService.checkNationalId(nationalId);
        return ResponseEntity.ok(!exists);
    }

    @PostMapping("/set-user-avatar")
    public ResponseEntity<Map<String, Object>> setUserAvatar(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "avatar") MultipartFile avatar,
            HttpSession httpSession) {
        User user = userRepo.getUserById(userId);
        if(user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        userService.setUserAvatar(user, avatar);
        User logedUser = (User)  httpSession.getAttribute("user");
        // Update avatar in session if the user is the loged user
        if(Objects.equals(user.getId(), logedUser.getId())) {
            logedUser.setAvatar(user.getAvatar());
            httpSession.setAttribute("user", logedUser);
        }
        return ResponseEntity.ok(Map.of("success", "Avatar updated"));
    }
}
