package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserAPI {

    @Autowired
    private UserRepo userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
