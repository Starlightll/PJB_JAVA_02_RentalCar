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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
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
    public String searchCar(Model model,
                            @RequestParam(value = "name", defaultValue = "", required = false) String name,
                            @RequestParam(value = "pickDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pickDate,
                            @RequestParam(value = "dropDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dropDate,
                            @RequestParam(value = "pickTime", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime pickTime,
                            @RequestParam(value = "dropTime", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime dropTime,
                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo ,
                            @RequestParam(defaultValue = "lastModified") String sortBy
                           ) {
        Sort sort;
        boolean findByStatus = false;
          switch (sortBy){
              case "newestToLatest":
                  sort = Sort.by(Sort.Direction.DESC, "lastModified");
                  break;
              case "latestToNewest":
                  sort = Sort.by(Sort.Direction.ASC, "lastModified");
                  break;
              case "priceLowToHigh":
                  sort = Sort.by(Sort.Direction.ASC, "basePrice");
                  break;
              case "priceHighToLow":
                  sort = Sort.by(Sort.Direction.DESC, "basePrice");
                  break;
              default:
                  sort = Sort.by(Sort.Direction.DESC, "lastModified");
                  break;
          }

        LocalDate today = LocalDate.now();
        LocalTime defaultTime = LocalTime.of(0, 0);

       // List<Car> carList = searchCarService.findCars(name);
        Page<Car> list = searchCarService.findCars(name,pageNo, sort);

        if (name.trim().isEmpty()) {
            model.addAttribute("sendLocation", "Please enter location");

        } else if (pickDate == null) {
            model.addAttribute("sendPickDate", "Please enter pick date");
        } else if (pickTime == null ) {
            model.addAttribute("sendPickTime", "Please enter pick time");
        } else if (dropDate == null) {
            model.addAttribute("sendDropDate", "Please enter drop date");
        } else if (dropTime == null) {
            model.addAttribute("sendDropTime", "Please enter drop time");
        } else if (pickDate.isEqual(dropDate)) {
            if (pickTime.isAfter(dropTime) || pickTime.equals(dropTime)) {
                model.addAttribute("sendCondition", "Drop-time must be after pick time");
            }else{

                model.addAttribute("list", list);
                model.addAttribute("totalPage", list.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("name", name);
                model.addAttribute("pickDate",pickDate);
                model.addAttribute("dropDate",dropDate);
                model.addAttribute("dropTime",dropTime);
                model.addAttribute("pickTime",pickTime);
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("size", list.getTotalElements());
            }
        } else if (pickDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Pick-up date must not be in the past");
        } else if (dropDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Drop-off date must not be in the past");
        } else if (pickDate.isAfter(dropDate)) {
            model.addAttribute("sendCondition", "Drop-off date and time must be later than Pick-up date and time");
        }else {
            model.addAttribute("list", list);
            model.addAttribute("totalPage", list.getTotalPages());
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("name", name);
            model.addAttribute("pickDate",pickDate);
            model.addAttribute("dropDate",dropDate);
            model.addAttribute("dropTime",dropTime);
            model.addAttribute("pickTime",pickTime);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("size", list.getTotalElements());
        }

        return "products/Search_Car";
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