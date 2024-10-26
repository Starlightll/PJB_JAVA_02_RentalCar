package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.ForgotDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.VerificationToken;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.repository.VerificationTokenRepo;
import com.rentalcar.rentalcar.service.ForgotPasswordService;
import com.rentalcar.rentalcar.service.RegisterUserService;
import com.rentalcar.rentalcar.service.VerificationTokenService;
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
public class ForgotPassController {

    @Autowired
    ForgotPasswordService forgotPasswordService;

    @Autowired
    VerificationTokenService verificationTokenService;

    //============= FORGOT PASSWORD =============

    @GetMapping("/forgot-password")
    public String forgotPassWord(Model model)
    {
        model.addAttribute(new ForgotDto());
        return "password/ForgotPassword";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassWord(@Valid  @ModelAttribute ForgotDto forgotDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            model.addAttribute("forgotDto", forgotDto);
            return "password/ForgotPassword";
        }

        try {
            forgotPasswordService.forgotPassword(forgotDto.getEmail());
            // Thêm thông báo thành công vào RedirectAttributes
            redirectAttributes.addFlashAttribute("successMessage", "A password reset link has been sent to your email.");
            return "redirect:/forgot-password";
        }catch (UserException e) {
            switch (e.getMessage()) {
                case "Account not activated":
                    result.rejectValue("email", "error.email", "Your account is not activated");
                    break;
                case "Not found":
                    result.rejectValue("email", "error.email", "Email does not exists");
                    break;
                case "Account Locked":
                    result.rejectValue("email", "error.email", "Your account is locked");
                    break;
            }
            model.addAttribute("forgotDto", forgotDto);
            return "password/ForgotPassword";
        }

    }



    //============= RESET PASSWORD =============


    @GetMapping("/reset-password")
    public String resetPassWord(@RequestParam("token") String token, Model model) {
        String result = forgotPasswordService.checToken(token);

        // Thêm forgotDto vào model để Thymeleaf nhận diện đối tượng này.
        model.addAttribute("forgotDto", new ForgotDto());
        model.addAttribute("token", token);//Mỗi lần lỗi thì vẫn giữ được token này trên url
        model.addAttribute("showResendLink", false); // display form

        if (result.equals("Token expired")) {
            model.addAttribute("error", "Token has expired. Please request a new reset password link.");
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
    public String handleResetPassWord(@Valid  @ModelAttribute("forgotDto") ForgotDto forgotDto, BindingResult result, @RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("showResendLink", false); // display form

        // Don't need to check email
        if (result.hasFieldErrors("password") || result.hasFieldErrors("confirmPassword")) {
            model.addAttribute("forgotDto", forgotDto);
            return "password/ResetPassword";
        }



        try {

            VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
            String email = verificationToken.getUser().getEmail();

            forgotPasswordService.resetPassword(email, forgotDto.getPassword(), forgotDto.getConfirmPassword());
            model.addAttribute("success", "Reset password successful");
            return "password/ResetPassword";
        } catch (UserException e) {
            switch (e.getMessage()) {
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
                    break;
                case "Not found":
                    result.rejectValue("email", "error.email", "Email does not exists");
                    break;
            }
            model.addAttribute("forgotDto", forgotDto);
            return "password/ResetPassword";
        }

    }


}
