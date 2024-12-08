package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/")
public class homeController {

    @Autowired UserDetailsServiceImpl userDetailsService;

    @GetMapping({"/homepage-guest"})
    public String homePage() {
        return "HomepageForGuest";
    }


    //Phan quyen cho home tren navbar
    @GetMapping("/")
    public String homeRedirect(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Customer"))) {
                return "redirect:/homepage-customer";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Car Owner"))) {
                return "redirect:/homepage-carowner";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"))) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/homepage-guest";
    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "UserManagement/SignIn";
    }


    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }



}
