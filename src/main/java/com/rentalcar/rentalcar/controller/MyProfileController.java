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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;


@Controller
public class MyProfileController {

    @Autowired
    MyProfileService myProfileService;
    @Autowired
    private UserService userService;


    @GetMapping("/change-password")
    public String changePassword(Model model, HttpSession session) {
        model.addAttribute("activeTab", "Security"); // Set active tab to Security

        if (!model.containsAttribute("myProfileDto")) {
            model.addAttribute("myProfileDto", new MyProfileDto());
        }
        User user = (User) session.getAttribute("user");

        //truyền dữ liệu cho personal in4
        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleChangePassword(
            @Valid @ModelAttribute("myProfileDto") MyProfileDto myProfileDto,
            BindingResult result, HttpSession session) {

        Map<String, String> response = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi 400 kèm thông báo lỗi
        }

        try {
            myProfileService.changePassword(myProfileDto, session);
            response.put("success", "Your password has been changed successfully!");
            return ResponseEntity.ok(response); // Trả về thành công
        } catch (UserException ex) {
            switch (ex.getMessage()) {
                case "User not found":
                    response.put("general", "User not found.");
                    break;
                case "Old password is incorrect":
                    result.rejectValue("oldPassword", "error.oldPassword", "Current password is incorrect");
                    break;
                case "Passwords do not match":
                    result.rejectValue("confirmPassword", "error.confirmPassword", "New password and Confirm password don’t match");
                    break;
            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response); // Trả về lỗi kèm thông báo lỗi
        }
    }




    @GetMapping("/my-profile")
    public String myProfile(Model model, HttpSession session) {
        model.addAttribute("activeTab", "PersonalInfo"); // Set default tab
        model.addAttribute("myProfileDto", new MyProfileDto());
        User user = (User) session.getAttribute("user");

        model.addAttribute("userInfo", user);
        return "MyProfile_ChangPassword";
    }


    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserInfoDto userInfoRequest, HttpSession session , RedirectAttributes model) {
        userService.saveUser(userInfoRequest, session);
        model.addFlashAttribute("success1","Update successfully!!!");
        return "redirect:/my-profile";
    }

}
