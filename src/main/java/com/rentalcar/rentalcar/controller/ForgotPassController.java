package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.ForgotDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.VerificationTokenRepo;
import com.rentalcar.rentalcar.service.ForgotPasswordService;
import com.rentalcar.rentalcar.service.RegisterUserService;
import com.rentalcar.rentalcar.service.VerificationTokenService;
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
public class ForgotPassController {

    @Autowired
    ForgotPasswordService forgotPasswordService;

    @Autowired
    VerificationTokenService verificationTokenService;

    //============= FORGOT PASSWORD =============

    @GetMapping("/forgot-password")
    public String forgotPassWord(Model model)
    {
        model.addAttribute("forgotDto",new ForgotDto());
        return "password/ForgotPassword";
    }



    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleForgotPassWord(@Valid  @ModelAttribute("forgotDto") ForgotDto forgotDto, BindingResult result) {

        Map<String, String> response = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi 400 kèm thông báo lỗi
        }



        try {
            forgotPasswordService.forgotPassword(forgotDto.getEmail());
            response.put("success", "A password reset link has been sent to your email.");
            return ResponseEntity.ok(response); // Trả về thành công
        }catch (UserException e) {
            switch (e.getMessage()) {
                case "Account not activated":
                    result.rejectValue("email", "error.email", "Your account is not activated");
                    break;
                case "Not found":
                    result.rejectValue("email", "error.email", "The email address you’ve entered does not exist. Please try again");
                    break;
                case "Account Locked":
                    result.rejectValue("email", "error.email", "Your account is locked");
                    break;
            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi kèm thông báo lỗi
        }

    }



    //============= RESET PASSWORD =============


    @GetMapping("/reset-password")
    public String resetPassWord(@RequestParam("token") String token, Model model) {
        String result = forgotPasswordService.checToken(token);

        // Thêm forgotDto vào model để Thymeleaf nhận diện đối tượng này. dùng chung với forgot
        model.addAttribute("forgotDto", new ForgotDto());
        model.addAttribute("token", token);//Mỗi lần lỗi thì vẫn giữ được token này trên url


        if (result.equals("Token expired")) {
            model.addAttribute("error", "This link has expired. Please go back to Homepage and try again.");
            model.addAttribute("showResendLink", true);
            return "password/ResetPassword";
        }

        if(result.equals("Invalid token")) {
            model.addAttribute("error", "Invalid token.");
            model.addAttribute("showResendLink", true);
            return "password/ResetPassword";
        }


        return "password/ResetPassword";
    }



    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleResetPassWord(@Valid @ModelAttribute("forgotDto") ForgotDto forgotDto, BindingResult result, @RequestParam("token") String token, Model model) {

        Map<String, String> response = new HashMap<>();

        // Don't need to check email
        if (result.hasFieldErrors("password") || result.hasFieldErrors("confirmPassword")) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));

            return ResponseEntity.badRequest().body(response);
        }

        try {

            VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
            String email = verificationToken.getUser().getEmail();

            forgotPasswordService.resetPassword(email, forgotDto.getPassword(), forgotDto.getConfirmPassword());
            response.put("success", "Reset password successful.");
            return ResponseEntity.ok(response); // Trả về thành công
        } catch (UserException e) {
            switch (e.getMessage()) {
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Password and Confirm password don’t match. Please try again.");
                    break;
                case "Not found":
                    result.rejectValue("email", "error.email", "The email address you’ve entered does not exist. Please try again");
                    break;
            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi kèm thông báo lỗi
        }

    }


}
