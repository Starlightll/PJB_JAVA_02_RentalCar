package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


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




    @GetMapping("/addcar")
    public String addCar() {
        return "carowner/AddCar";
    }

   @GetMapping("/my-feedback")
    public String myFeedback() {
        return "feedback/MyFeedback";
    }

}
