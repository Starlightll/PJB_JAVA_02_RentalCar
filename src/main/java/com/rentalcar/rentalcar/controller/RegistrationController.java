package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.ForgotDto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String registerUser(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("hasSignupErrors", true);
            model.addAttribute("registerDto", registerDto);
            return "UserManagement/SignIn";
        }

        try {
            userService.registerUser(registerDto);
        }catch (UserException e) {
            switch (e.getMessage()) {
                case "Email already exists":
                    result.rejectValue("email", "error.email", "Email already exists");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
                    break;
            }
            model.addAttribute("hasSignupErrors", true);
            model.addAttribute("registerDto", registerDto);
            return "UserManagement/SignIn";

        }catch (IllegalArgumentException ex) {
            if(ex.getMessage().equals("Phone number already exists")) {
                result.rejectValue("phoneNumber", "error.phoneNumber", "Phone number already exists");
                model.addAttribute("hasSignupErrors", true);
                model.addAttribute("registerDto", registerDto);
                return "UserManagement/SignIn";
            }
        }
        return "redirect:/login";
    }



    @GetMapping("/register/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {

        String result = userService.confirmToken(token);

        if (result.equals("Token valid")) {
            model.addAttribute("message", "Account activated successfully.");
        } else if (result.equals("Token expired")) {
            model.addAttribute("error", "Token has expired. Please request a new activation link.");
        } else if(result.equals("Account activated.")){
            model.addAttribute("message", "Account is activated.");
        } else {
            model.addAttribute("error", "Invalid token.");
        }
        return "UserManagement/activationResult";
    }

//    @PostMapping("/register/resendToken")
//    public String resendToken(@RequestParam("email") String email, Model model) {
//        try {
//            userService.resendActivationToken(email); // Gửi lại token
//            model.addAttribute("message", "A new activation link has been sent to your email.");
//        } catch (UserException e) {
//            model.addAttribute("message", "Error: " + e.getMessage());
//        }
//        return "UserManagement/activationResult";
//    }



    @GetMapping("send-activation")
    public String sendActivation(Model model) {
        model.addAttribute("forgotDto", new RegisterDto());
        return "UserManagement/send-activation";
    }

    @PostMapping("send-activation")
    public String handleSendActivation(@Valid @ModelAttribute ForgotDto forgotDto, BindingResult result, Model model,  RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            model.addAttribute("forgotDto", forgotDto);
            return "UserManagement/send-activation";
        }
        try {
            userService.resendActivationToken(forgotDto.getEmail()); // Gửi lại token
            // Thêm thông báo thành công vào RedirectAttributes
            redirectAttributes.addFlashAttribute("successMessage", "A new activation link has been sent to your email.");
            return "redirect:/send-activation";
        } catch (UserException e) {
            switch (e.getMessage()) {
                case "User not found.":
                    result.rejectValue("email", "error.email", "Email does not exists");
                    break;
                case "Account is already activated.":
                    result.rejectValue("email", "error.email", "Account is already activated.");
                    break;
            }
            model.addAttribute("forgotDto", forgotDto);

            return "UserManagement/send-activation";
        }


    }


}
