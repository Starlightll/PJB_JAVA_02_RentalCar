package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.service.MyProfileService;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
        model.addAttribute("userInfo", new UserInfoDto());
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

        if (bindingResult.hasErrors()) {
            // Return the view with the current user info to display errors
             model.addAttribute("userInfo", userInfoRequest);
            return "MyProfile_ChangPassword"; // Ensure this is the correct view name
        }

        userService.saveUser(userInfoRequest, session);
        models.addFlashAttribute("successfully", "Update successfully!!!");
        return "redirect:/my-profile";
    }


}
