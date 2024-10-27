package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.request.UserInfoRequest;
import com.rentalcar.rentalcar.response.UserInfoResponse;
import com.rentalcar.rentalcar.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class UserController {
    private final IUserService userService;


    @GetMapping("/userProfile")
    public String userProfile(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        UserInfoResponse userInfo = userService.getUserInfo(UserInfoRequest.builder().Id(user.getId()).build());


        model.addAttribute("userInfo", userInfo);

        return "MyProfile_ChangPassword";
    }
    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserInfoResponse userInfoRequest,HttpSession session , RedirectAttributes model) {

        User user = (User) session.getAttribute("user");
        user.setFullName(userInfoRequest.getFullName());
        user.setDob(userInfoRequest.getDob());
        user.setPhone(userInfoRequest.getPhone());
        user.setNationalId(userInfoRequest.getNationalId());
        user.setDrivingLicense(userInfoRequest.getDrivingLicense());
        user.setCity(userInfoRequest.getCity());
        user.setDistrict(userInfoRequest.getDistrict());
        user.setWard(userInfoRequest.getWard());
        user.setStreet(userInfoRequest.getStreet());


        userService.saveUser(user);
        model.addFlashAttribute("success","Update successfully!!!");
        return "redirect:/userProfile";
    }



}
