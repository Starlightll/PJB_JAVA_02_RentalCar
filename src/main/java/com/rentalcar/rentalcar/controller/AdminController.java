package com.rentalcar.rentalcar.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @GetMapping({"/admin/dashboard", "/admin"})
    public String adminDashboard() {
        return "/admin/index";
    }

    @GetMapping("/admin/user-list")
    public String userList() {
        return "/admin/app-user-list";
    }
}
