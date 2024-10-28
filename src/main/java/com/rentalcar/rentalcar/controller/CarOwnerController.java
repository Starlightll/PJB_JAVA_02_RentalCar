package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class CarOwnerController {

    @Autowired
    CarOwnerService carOwnerService;

    @Autowired
    CarDraftService carDraftService;
    @Autowired
    private HttpSession httpSession;

    @GetMapping("/my-cars")
    public String myCar() {
        return "/carowner/MyCars";
    }

    @GetMapping("/add-car")
    public ResponseEntity<?> addCar(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(carDraftService.getDraftByLastModified(user.getId()) != null) {
            return ResponseEntity.ok("You have a draft car, do you want to continue?");
        }
        return ResponseEntity.ok("New Car");
    }

    @PostMapping("/add-car")
    public String addCarPost() {
        return "carowner/MyCars";
    }

    @GetMapping("/edit-car")
    public String editCar() {
        return "carowner/EditCar";
    }

    @GetMapping("/delete-car")
    public String deleteCar() {
        return "carowner/MyCars";
    }



}
