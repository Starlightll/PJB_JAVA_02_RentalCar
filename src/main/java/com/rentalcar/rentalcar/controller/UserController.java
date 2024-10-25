package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.request.UserInfoRequest;
import com.rentalcar.rentalcar.response.UserInfoResponse;
import com.rentalcar.rentalcar.service.IUserService;
import com.rentalcar.rentalcar.service.impl.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
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
}
