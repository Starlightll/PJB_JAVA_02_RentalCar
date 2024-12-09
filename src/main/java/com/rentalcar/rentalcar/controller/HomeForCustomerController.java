package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;



@Controller
public class HomeForCustomerController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping("/homepage-customer")
    public String customerHomepage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        List<Object[]> topCars = carRepository.findTop5CarsWithHighestRating();

        List<CarDto> carDtoList = new ArrayList<>();
        for (Object[] car : topCars) {
            Long carId = ((Number) car[0]).longValue();
            String name = (String) car[1];
            String modelName = (String) car[2];
            Double basePrice = ((Number) car[3]).doubleValue();
            String front = (String) car[4];
            String back = (String) car[5];
            String left = (String) car[6];
            String right = (String) car[7];
            Double averageRating = (car[8] != null) ? ((Number) car[8]).doubleValue() : 0.0;


            CarDto carDto = new CarDto();
            carDto.setCarId(carId);
            carDto.setName(name);
            carDto.setModel(modelName);
            carDto.setBasePrice(basePrice);
            carDto.setFront(front);
            carDto.setBack(back);
            carDto.setLeft(left);
            carDto.setRight(right);
            carDto.setAverageRating(averageRating);

            carDtoList.add(carDto);
        }

        model.addAttribute("topCars", carDtoList);

        return "HomepageForCustomer";
    }


     @GetMapping("/homepage-customer/my-profile")
    public String myProfileHomepage() {
        return "MyProfile_ChangPassword";
     }

}
