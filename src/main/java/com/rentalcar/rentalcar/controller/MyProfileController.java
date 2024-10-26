package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.service.MyProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class MyProfileController {

    @Autowired
    MyProfileService myProfileService;


    @GetMapping("/my-profile")
    public String myProfile_changPass(Model model) {
        model.addAttribute("hasReSetErrors", true);
        model.addAttribute("myProfileDto", new MyProfileDto());
        return "MyProfile_ChangPassword";
    }


    @PostMapping("/my-profile")
    public String handleMyProfile(@Valid @ModelAttribute("myProfileDto") MyProfileDto myProfileDto, BindingResult result, Model model,
        HttpSession session) {
        if(result.hasErrors()) {
            model.addAttribute("myProfileDto", myProfileDto);
            return "MyProfile_ChangPassword";
        }

        try {
            myProfileService.changePassword(myProfileDto, session);
            model.addAttribute("success", "Your password has been changed successfully!");
            return "MyProfile_ChangPassword";
        }catch (UserException ex){
            switch (ex.getMessage()) {
                case "User not found":
                    model.addAttribute("error", "User not found");
                    break;
                case "Old password is incorrect":
                    result.rejectValue("oldPassword", "error.oldPassword", "Old password is incorrect");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
                    break;
            }
            model.addAttribute("myProfileDto", myProfileDto);
            return "MyProfile_ChangPassword";
        }

    }

}
