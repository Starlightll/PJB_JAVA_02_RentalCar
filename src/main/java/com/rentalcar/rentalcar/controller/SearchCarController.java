package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.service.SearchCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SearchCarController {
    @Autowired
    private SearchCarService searchCarService;

    @GetMapping("/searchCar")
    public String searchCar(Model model, @Param("name") String name, @Param("pickDate") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate pickDate, @Param("dropDate") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate dropDate) {



        LocalDate today = LocalDate.now();

        if (pickDate == null) {
            model.addAttribute("send", "Please enter pick-up date and time");
        } else if (dropDate == null) {
            model.addAttribute("send", "Please enter drop-off date and time");
        } else if (pickDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Pick-up date must not be in the past");
        } else if (dropDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Drop-off date must not be in the past");
        } else if (pickDate.isAfter(dropDate)) {
            model.addAttribute("sendCondition", "Drop-off date and time must be later than Pick-up date and time");
        } else {
            List<Car> list = searchCarService.findCars(name);
            model.addAttribute("list", list);
        }

        return "HomePageForCustomer";

    }
}
