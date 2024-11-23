package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserAPI {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean exists = userService.checkEmail(email);
        return ResponseEntity.ok(!exists);
    }
}
