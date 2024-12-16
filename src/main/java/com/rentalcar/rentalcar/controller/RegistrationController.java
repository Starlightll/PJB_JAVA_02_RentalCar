package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.ForgotDto;
import com.rentalcar.rentalcar.dto.RegisterDto;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.VerificationTokenRepo;
import com.rentalcar.rentalcar.service.RegisterUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;


@Controller
public class RegistrationController {
    @Autowired
    private RegisterUserService userService;

    @Autowired
    private VerificationTokenRepo tokenRepo;


    @GetMapping("/register")
    public String registerPage(Model model) {
        if(!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto());
        }
        return "UserManagement/SignIn";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterDto registerDto, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {
        // Nếu người dùng chưa tích sẽ báo lỗi
        if(!registerDto.isAgreedTerms()) {
            redirectAttributes.addFlashAttribute ("hasSignupErrors", true);
            result.rejectValue("agreedTerms", "error.agreedTerms", "You must agree to the terms");
            redirectAttributes.addFlashAttribute ("registerDto", registerDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerDto", result);

            return "redirect:/register";
        }

        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute ("hasSignupErrors", true);
            registerDto.setAgreedTerms(false); // Đặt mặc định là false
            redirectAttributes.addFlashAttribute ("registerDto", registerDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerDto", result);

            return "redirect:/register";
        }

        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute ("success", true);

        }catch (UserException e) {
            switch (e.getMessage()) {
                case "Email already exists":
                    result.rejectValue("email", "error.email", "Email already exists");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Password and Confirm password don’t match. Please try again.");
                    break;
                case "Username already exists":
                    result.rejectValue("username", "error.username", "Username already exists");
                    break;
            }
            registerDto.setAgreedTerms(false); // Đặt mặc định là false
            redirectAttributes.addFlashAttribute ("hasSignupErrors", true);
            redirectAttributes.addFlashAttribute ("registerDto", registerDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerDto", result);

            return "redirect:/register";

        }catch (IllegalArgumentException ex) {

            switch (ex.getMessage()) {
                case "Phone number already exists":
                    result.rejectValue("phoneNumber", "error.phoneNumber", "Phone number already exists");
                    break;
                case "Invalid phone number format":
                    result.rejectValue("phoneNumber", "error.phoneNumber", "Invalid phone number format");
                    break;
            }
            redirectAttributes.addFlashAttribute ("hasSignupErrors", true);
            registerDto.setAgreedTerms(false); // Đặt mặc định là false
            redirectAttributes.addFlashAttribute ("registerDto", registerDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerDto", result);
            return "redirect:/register";
        }
        return "redirect:/login?success";
    }



    @GetMapping("/register/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {

        String result = userService.confirmToken(token);

        if (result.equals("Token valid")) {
            model.addAttribute("message", "Account activated successfully.");
        } else if (result.equals("Token expired")) {
            model.addAttribute("error", "This link has expired. Please go back to Homepage and try again.");
        } else if(result.equals("Account activated.")){
            model.addAttribute("message", "Account is activated.");
        } else {
            model.addAttribute("error", "Invalid token.");
        }
        return "UserManagement/activationResult";
    }




    @GetMapping("send-activation")
    public String sendActivation(Model model) {
        model.addAttribute("forgotDto", new RegisterDto());
        return "UserManagement/send-activation";
    }

    @PostMapping("send-activation")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleSendActivation(@Valid @ModelAttribute ForgotDto forgotDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        Map<String, String> response = new HashMap<>();
        if(result.hasErrors()) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            userService.resendActivationToken(forgotDto.getEmail()); // Gửi lại token
            // Thêm thông báo thành công vào RedirectAttributes
            response.put("success", "A new activation link has been sent to your email.");
            return ResponseEntity.ok(response); // Trả về thành công

        } catch (UserException e) {
            switch (e.getMessage()) {
                case "User not found.":
                    result.rejectValue("email", "error.email", "The email address you’ve entered does not exist. Please try again");
                    break;
                case "Account is already activated.":
                    result.rejectValue("email", "error.email", "Account is already activated.");
                    break;
            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi kèm thông báo lỗi
        }


    }


    @GetMapping("/agree-term-service")
    public String agreeTermService(Model model) {
        return "UserManagement/TermsOfService";
    }


}
