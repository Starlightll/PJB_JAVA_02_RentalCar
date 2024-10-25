package com.rentalcar.rentalcar.controller;


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

import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private RegisterUserService userService;

    @Autowired
    private VerificationTokenRepo tokenRepo;


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "UserManagement/SignIn";
    }


    @PostMapping("/register")
    public String registerUser(@Valid RegisterDto registerDto, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("registerDto", registerDto);
            return "UserManagement/SignIn";
        }

        try {
            userService.registerUser(registerDto);
            return "redirect:/login";
        }catch (UserException e) {
            switch (e.getMessage()) {
                case "Email already exists":
                    result.rejectValue("email", "error.email", "Email already exists");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
                    break;
            }
            model.addAttribute("registerDto", registerDto);
            return "UserManagement/SignIn";
        }
    }



    @GetMapping("/register/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {

        String result = userService.confirmToken(token);

        if (result.equals("Token valid")) {
            model.addAttribute("message", "Account activated successfully.");
        } else if (result.equals("Token expired")) {
            model.addAttribute("message", "Token has expired. Please request a new activation link.");
            model.addAttribute("showResendLink", true);

            VerificationToken verificationToken = tokenRepo.findByToken(token);
            User user = verificationToken.getUser();
            model.addAttribute("email", user.getEmail());
        } else if(result.equals("Account activated.")){
            model.addAttribute("message", "Account is activated.");
        } else {
            model.addAttribute("error", "Invalid token.");
        }
        return "UserManagement/activationResult";
    }

    @PostMapping("/register/resendToken")
    public String resendToken(@RequestParam("email") String email, Model model) {
        try {
            userService.resendActivationToken(email); // Gửi lại token
            model.addAttribute("message", "A new activation link has been sent to your email.");
        } catch (UserException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "UserManagement/activationResult";
    }



}
