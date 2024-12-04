package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@CrossOrigin
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

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
            User newUser = userService.addUser(user);
            userService.setUserRole(newUser, Integer.parseInt(role));
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "User added successfully");
        return ResponseEntity.ok(response);
    }


}
