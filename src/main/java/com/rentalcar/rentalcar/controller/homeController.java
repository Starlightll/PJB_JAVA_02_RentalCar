package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class homeController {

    @Autowired UserDetailsServiceImpl userDetailsService;

    @GetMapping({"/", "/homepage-guest"})
    public String homePage() {
        return "HomepageForGuest";
    }


    @GetMapping("/homepage-customer")
    public String customerHomepage() {
        return "HomepageForCustomer"; // Return the view for customer
    }

    @GetMapping("/homepage-carowner")
    public String carOwnerHomepage() {
        return "HomepageForCarOwner"; // Return the view for car owner
    }


    @GetMapping("/login")
    public String loginPage() {
        return "UserManagement/Login";
    }


    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }


    @GetMapping("/addcar")
    public String addCar() {
        return "carowner/AddCar";
    }

    @GetMapping("/myProfile")
    public String myProfile_changPass() {
        return "MyProfile_ChangPassword";
    }
}
