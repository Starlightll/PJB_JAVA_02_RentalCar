package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@CrossOrigin
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/user-management/user-detail")
    public String userDetail(
            @RequestParam(value = "userId", required = false) Long id,
            Model model
    ) {
        if (id != null) {
            User user = userRepo.findById(id).orElse(null);
            if(user != null) {
                model.addAttribute("user", user);
            }else{
                return "redirect:/admin/user-list";
            }
        }
        return "/admin/app-user-view-account";
    }

    @GetMapping("/user-management/add-user")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "/admin/app-user-list";
    }

    @PostMapping("/user-management/add-user")
    public String addUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "formRole", required = true) String role,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "redirect:/admin/user-list";
        }
        try{
            User newUser = userService.addUser(user);
            userService.setUserRole(newUser, Integer.parseInt(role));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/admin/user-list";
    }


}
