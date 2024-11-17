package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
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
            userRepo.findById(id).ifPresent(user -> model.addAttribute("user", user));
        }
        return "/admin/app-user-view-account";
    }

}
