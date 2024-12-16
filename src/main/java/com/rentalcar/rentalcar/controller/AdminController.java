package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;

/**
 * Controller that handles various admin-related endpoints for managing
 * users, cars, and administrative functions.
 *
 * This controller provides endpoints to render the admin dashboard, user list,
 * profile settings, account settings, and car verification and update request
 * previews, among others.
 */
@Controller
public class AdminController {

    @Autowired
    CarDraftRepository carDraftRepository;
    @Autowired
    CarRepository carRepository;

    @GetMapping({"/admin/dashboard", "/admin"})
    public String adminDashboard() {
        return "/admin/index";
    }

    @GetMapping("/admin/user-list")
    public String userList(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/admin/app-user-list";
    }

    @GetMapping("/admin/my-profile")
    public String myProfile() {
        return "/admin/pages-profile-user";
    }

    @GetMapping("/admin/account-settings")
    public String accountSettings(
            Model model
    ) {
        return "/admin/pages-account-settings-account";
    }

    @GetMapping("/admin/car/car-verify")
    public String carVerify(Model model) {
        return "/admin/app-car-verify";
    }

    @GetMapping("/admin/testEmail")
    public String testEmail() {
        return "/emailtemplate/car-approved";
    }

    @GetMapping("/admin/car/car-update-request-list")
    public String carUpdateRequestList(
            Model model
    ) {
        return "/admin/app-car-update-request-list";
    }

    @GetMapping("/admin/car-update-request-preview")
    public String carUpdateRequestPreview(
            Model model,
            @RequestParam(value = "requestId") Integer draftId
    ) {
        CarDraft carDraft = carDraftRepository.findCarDraftsByDraftIdAndVerifyStatus(draftId, "Pending");
        if(carDraft == null) {
           throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        }
        Car car = carRepository.getCarByCarId(carDraft.getCarId());
        model.addAttribute("car", car);
        model.addAttribute("carDraft", carDraft);
        return "/admin/app-car-update-request-preview";
    }
}
