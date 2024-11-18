package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping({"/admin/dashboard", "/admin"})
    public String adminDashboard() {
        return "/admin/index";
    }

    @GetMapping("/admin/user-list")
    public String userList(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/admin/app-user-list";
    }

    @GetMapping("/admin/my-profile")
    public String myProfile() {
        return "/admin/pages-profile-user";
    }

    @GetMapping("/admin/account-settings")
    public String accountSettings() {
        return "/admin/pages-account-settings-account";
    }
}
