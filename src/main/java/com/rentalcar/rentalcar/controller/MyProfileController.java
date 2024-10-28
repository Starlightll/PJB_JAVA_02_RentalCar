package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.service.MyProfileService;
import com.rentalcar.rentalcar.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class MyProfileController {

    @Autowired
    MyProfileService myProfileService;
    @Autowired
    private UserService userService;


    @GetMapping("/my-profile")
    public String myProfile(Model model, HttpSession session) {
        model.addAttribute("activeTab", "PersonalInfo"); // Set default tab
        model.addAttribute("myProfileDto", new MyProfileDto());
        User user = (User) session.getAttribute("user");

        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, HttpSession session) {
        model.addAttribute("activeTab", "Security"); // Set active tab to Security
        model.addAttribute("myProfileDto", new MyProfileDto());
        User user = (User) session.getAttribute("user");

        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }

    @PostMapping("/change-password")
    public String handleMyProfile(@Valid @ModelAttribute("myProfileDto") MyProfileDto myProfileDto, BindingResult result, Model model,
                                  HttpSession session) {

        model.addAttribute("activeTab", "Security");

        User user = (User) session.getAttribute("user");
        model.addAttribute("userInfo", user);

        if(result.hasErrors()) {
            model.addAttribute("myProfileDto", myProfileDto);
            return "MyProfile_ChangPassword";
        }

        try {
            myProfileService.changePassword(myProfileDto, session);
            model.addAttribute("success2", "Your password has been changed successfully!");
            return "MyProfile_ChangPassword";
        }catch (UserException ex){
            switch (ex.getMessage()) {
                case "User not found":
                    model.addAttribute("error", "User not found");
                    break;
                case "Old password is incorrect":
                    result.rejectValue("oldPassword", "error.oldPassword", "Current password is incorrect");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "New password and Confirm password don’t match");
                    break;
            }
            model.addAttribute("myProfileDto", myProfileDto);
            return "MyProfile_ChangPassword";
        }

    }


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userInfo") UserInfoDto userInfoRequest,
                           BindingResult bindingResult,
                           HttpSession session,
                           RedirectAttributes models , Model model) {
        String dobError = null;
        String emailError = null;
        String nationalIdError = null;
        String phoneError = null;
        String drivingLicenseError = null;
        String streetError = null;
        String fullNameError = null;
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                switch (error.getField()) {
                    case "dob":
                        dobError = error.getDefaultMessage();
                        break;
                    case "email":
                        emailError = error.getDefaultMessage();
                        break;
                    case "nationalId":
                        nationalIdError = error.getDefaultMessage();
                        break;
                    case "phone":
                        phoneError = error.getDefaultMessage();
                        break;
                    case "drivingLicense":
                        drivingLicenseError = error.getDefaultMessage();
                        break;
                    case "street":
                        streetError = error.getDefaultMessage();
                        break;
                    case "fullName":
                        fullNameError = error.getDefaultMessage();
                        break;
                }
            }

            // Thêm các thông điệp lỗi vào model để hiển thị lại
            models.addFlashAttribute("dobError", dobError);
            models.addFlashAttribute("emailError", emailError);
            models.addFlashAttribute("nationalIdError", nationalIdError);
            models.addFlashAttribute("phoneError", phoneError);
            models.addFlashAttribute("drivingLicenseError", drivingLicenseError);
            models.addFlashAttribute("streetError", streetError);
            models.addFlashAttribute("fullNameError", fullNameError);
            //User user = (User) session.getAttribute("user");

            // model.addAttribute("userInfo", user);
            // Return the view with the current user info to display errors
            //  model.addAttribute("userInfo", userInfoRequest);
            // return "MyProfile_ChangPassword"; // Ensure this is the correct view name

            return "redirect:/my-profile";
        }

        userService.saveUser(userInfoRequest, session);
        models.addFlashAttribute("success1", "Update successfully!!!");
        return "redirect:/my-profile";
    }

}
