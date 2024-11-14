package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.RentalCarService;
import com.rentalcar.rentalcar.service.ViewEditBookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingDetailsController {


    @Autowired
    RentalCarService rentalCarService;

    @Autowired
    UserRepo userRepo;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private CarStatusRepository carStatusRepository;

    @Autowired
    private ViewEditBookingService viewEditBookingService;

    @GetMapping("/homepage-customer/booking-detail")
    public String bookingDetail(@RequestParam Integer bookingId,@RequestParam Integer carId,  Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        MyBookingDto booking = viewEditBookingService.getBookingDetail(bookingId, carId,session);
        List<UserDto> listDriver = getAllDriverAvailable();

        //=========================================================== Car detail ===================================================

        Car car = carRepository.getCarByCarId(carId);
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
        //=====================================================================================================

        model.addAttribute("users", listDriver);
        model.addAttribute("car", car);
        model.addAttribute("user", user);
        model.addAttribute("booking", booking);

        return "customer/EditBookingDetails";
    }

    public List<UserDto> getAllDriverAvailable() {
        List<Object[]> results = userRepo.getAllDriverAvailable();
        List<UserDto> userDtos = new ArrayList<>();

        for (Object[] result : results) {
            Long userId = ((Number) result[0]).longValue();
            String fullName = (String) result[1];
            LocalDate dob = null;
            if (result[2] != null) {
                java.sql.Date sqlDate = (java.sql.Date) result[2];
                dob = sqlDate.toLocalDate();
            }

            UserDto userDto = new UserDto(userId, fullName, dob);
            userDtos.add(userDto);
        }

        return userDtos;
    }
}
