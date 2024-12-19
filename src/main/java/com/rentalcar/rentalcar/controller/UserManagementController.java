package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.FileStorageService;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * The UserManagementController class provides RESTful endpoints for managing user accounts within the admin section.
 * It supports operations like viewing user details, adding, updating, deleting, and suspending users.
 * The class is annotated with @Controller to mark it as a Spring MVC controller and uses @RequestMapping to map all HTTP requests
 * with the path "/admin" onto methods in this controller. Cross-origin resource sharing (CORS) is configured
 * using the @CrossOrigin annotation.
 */

@Controller
@RequestMapping("/admin")
@CrossOrigin
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/user-management/user-detail")
    public String userDetail(
            @RequestParam(value = "userId", required = false) Long id,
            Model model
    ) {
        if (id != null) {
            User user = userRepo.findById(id).orElse(null);
            if(user != null) {
                model.addAttribute("user", user);
            }else{
                return "redirect:/admin/user-list";
            }
        }
        return "/admin/app-user-view-account";
    }

    @GetMapping("/user-management/add-user")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "/admin/app-user-list";
    }

    @PostMapping("/user-management/add-user")
    public ResponseEntity<Map<String, Object>> addUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "formRole", required = true) String role,
            BindingResult result
    ) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        try{
            if (Integer.parseInt(role) == 4) {
                user.setSalaryDriver(499000.00);
            }
            userService.addUser(user, Integer.parseInt(role));
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User added successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user-management/update-user")
    public ResponseEntity<Map<String, Object>> updateUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "drivingLicenseFile", required = false) MultipartFile drivingLicenseFile,
            BindingResult result
    ) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        if(user.getId() == null){
            response.put("error", "User ID is required");
            return ResponseEntity.badRequest().body(response);
        }
        try{
            userService.updateUser(user, drivingLicenseFile);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-management/delete-user")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @RequestParam(value = "userId", required = true) Long userId
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getStatus() == UserStatus.DELETED){
                response.put("error", "User already deleted");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getStatus() == UserStatus.LOCKED){
                response.put("error", "User is locked");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getRoles().get(0).getId() == 1){
                response.put("error", "Cannot delete admin user");
                return ResponseEntity.badRequest().body(response);
            }
            userService.setUserStatus(user, UserStatus.DELETED);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-management/suspend-user")
    public ResponseEntity<Map<String, Object>> suspendUser(
            @RequestParam(value = "userId", required = true) Long userId
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                response.put("error", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getStatus() == UserStatus.DELETED){
                response.put("error", "User already deleted");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getStatus() == UserStatus.LOCKED){
                response.put("error", "User is locked");
                return ResponseEntity.badRequest().body(response);
            }
            if(user.getRoles().get(0).getId() == 1){
                response.put("error", "Cannot suspend admin user");
                return ResponseEntity.badRequest().body(response);
            }
            userService.setUserStatus(user, UserStatus.LOCKED);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User locked successfully");
        return ResponseEntity.ok(response);
    }


}
