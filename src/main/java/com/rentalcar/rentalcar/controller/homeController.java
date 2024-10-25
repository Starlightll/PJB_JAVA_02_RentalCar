package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.VerificationTokenRepo;
import com.rentalcar.rentalcar.service.RegisterUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String loginPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "UserManagement/SignIn";
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
