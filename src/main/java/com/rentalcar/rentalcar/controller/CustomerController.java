package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/booking")
    public String booking(
            @RequestParam(value = "carId", required = false) Integer carId,
            Model model
    ) {
        Car car = carRepository.getCarByCarId(carId);
        model.addAttribute("car", car);
        return "customer/booking";
    }
}
