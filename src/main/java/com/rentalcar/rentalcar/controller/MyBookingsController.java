package com.rentalcar.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.*;
import com.rentalcar.rentalcar.service.CarDraftService;
import com.rentalcar.rentalcar.service.CarOwnerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rentalcar.rentalcar.common.Regex.*;
import static com.rentalcar.rentalcar.common.Regex.LICENSE_PLATE_REGEX;

@Controller
public class MyBookingsController {
    @Autowired
    CarOwnerService carOwnerService;
    @Autowired
    CarDraftService carDraftService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private CarStatusRepository carStatusRepository;
    @Autowired
    private CarRepository carRepository;


    @GetMapping("/my-booking")
    public String myBooking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "lastModified") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean findByStatus = false;
        switch (sortBy) {
            case "newestToLatest":
                sortBy = "lastModified";
                order = "desc";
                break;
            case "latestToNewest":
                sortBy = "lastModified";
                order = "asc";
                break;
            case "priceLowToHigh":
                sortBy = "basePrice";
                order = "asc";
                break;
            case "priceHighToLow":
                sortBy = "basePrice";
                order = "desc";
                break;
            case "available", "booked":
                findByStatus = true;
                break;
            default:
                break;
        }
        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        Page<Car> carPage;
        if(findByStatus){
            int statusId = 0;
            if(sortBy.equalsIgnoreCase("available")){
                statusId = 1;
            }else{
                statusId = 2;
            }
            pageable = PageRequest.of(page-1, size);
            carPage = carRepository.findAllByCarStatusAndUser(statusId, user.getId(), pageable);
        }else{
            carPage = carRepository.findAllByUser(user, pageable);
        }
        List<Car> cars = carPage.getContent();
        if(cars.isEmpty()){
            model.addAttribute("message", "You have no cars");
        }else{
            model.addAttribute("carList", cars);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", carPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("order", order);
            model.addAttribute("size", size);
        }
        return "/customer/MyBookings";
    }




}
