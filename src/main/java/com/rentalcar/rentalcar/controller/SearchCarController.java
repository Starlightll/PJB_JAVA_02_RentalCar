package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.repository.CarStatusRepository;
import com.rentalcar.rentalcar.service.SearchCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
public class SearchCarController {
    @Autowired
    private SearchCarService searchCarService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CarStatusRepository carStatusRepository;

    @GetMapping("/searchCar")
    public String searchCar(Model model, @Param("name") String name, @Param("pickDate") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate pickDate, @Param("dropDate") @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate dropDate) {



        LocalDate today = LocalDate.now();

        if (name.trim().isEmpty()) {
            model.addAttribute("sendLocation", "Please enter location");
        } else if (dropDate == null) {
            model.addAttribute("send", "Please enter drop-off date and time");
        } else if (pickDate == null) {
            model.addAttribute("send", "Please enter pick-up date and time");

        } else if (pickDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Pick-up date must not be in the past");
        } else if (dropDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Drop-off date must not be in the past");
        } else if (pickDate.isAfter(dropDate)) {
            model.addAttribute("sendCondition", "Drop-off date and time must be later than Pick-up date and time");
        }
        else {
            List<Car> list = searchCarService.findCars(name);
            model.addAttribute("list", list);
            if (list != null) {
                model.addAttribute("size", list.size());
            } else {
                model.addAttribute("size", 0);
            }
        }

        return "HomePageForCustomer";

    }
    @GetMapping("/carDetail/{id}")
    public String carDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Car car = carRepository.getCarByCarId(id);
        if(car == null){
            return "redirect:/my-cars";
        }else{
            User user = (User) session.getAttribute("user");
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("additionalFunction", additionalFunctionRepository.findAll());
            model.addAttribute("carStatus", carStatusRepository.findAll());
            model.addAttribute("car", car);
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedBasePrice = df.format(car.getBasePrice() == null ? 0 : car.getBasePrice());
            String formattedCarPrice = df.format(car.getCarPrice() == null ? 0 : car.getCarPrice());
            String formattedDeposit = df.format(car.getDeposit() == null ? 0 : car.getDeposit());
            String formattedMileage = df.format(car.getMileage() == null ? 0 : car.getMileage());
            String formattedFuelConsumption = df.format(car.getFuelConsumption() == null ? 0 : car.getFuelConsumption());
            model.addAttribute("formattedCarPrice", formattedCarPrice);
            model.addAttribute("formattedDeposit", formattedDeposit);
            model.addAttribute("formattedMileage", formattedMileage);
            model.addAttribute("formattedFuelConsumption", formattedFuelConsumption);
            model.addAttribute("formattedBasePrice", formattedBasePrice);
            String registrationPath = car.getRegistration();
            String certificatePath = car.getCertificate();
            String insurancePath = car.getInsurance();
            model.addAttribute("registrationUrl", "/" + registrationPath);
            model.addAttribute("certificateUrl", "/" + certificatePath);
            model.addAttribute("insuranceUrl", "/" + insurancePath);
            List<CarStatus> carStatus = carStatusRepository.findAll();
            model.addAttribute("carStatuses", carStatus);

        }
        return "products/detail_product";

    }
}
