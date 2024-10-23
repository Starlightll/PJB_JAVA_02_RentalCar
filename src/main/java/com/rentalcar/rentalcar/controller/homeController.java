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

    @GetMapping("/")
    public String homePage() {
        return "HomepageForCustomer";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "UserManagement/Login";
    }

    @GetMapping("/login-success")
    public String loginSuccessPage(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        //Tìm người dùng thep email
        User user =  userDetailsService.loadUserByEmail(email);

        if(user != null) {
            session.setAttribute("user", user);

        }

        return "redirect:/";
    }


    @GetMapping("/forgotPass")
    public String forgotPassWord() {
        return "password/ForgotPassword";
    }

    @GetMapping("/resetPass")
    public String resetPassWord() {
        return "password/ResetPassword";
    }

    @GetMapping("/carowner")
    public String carOwner() {
        return "HomePageForCarOwner";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }
    
    @GetMapping("/forgotpassword")
    public String forgotpasswordPage() {
        return "login/forgotpassword";
    }

}
