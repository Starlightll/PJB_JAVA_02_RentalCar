package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.dto.AdditionalFunctionDto;
import com.rentalcar.rentalcar.dto.BrandDto;
import com.rentalcar.rentalcar.dto.CarDto1;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.mappers.AdditionalFunctionMapper;
import com.rentalcar.rentalcar.mappers.BrandMapper;
import com.rentalcar.rentalcar.mappers.CarMapper;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.repository.CarStatusRepository;
import com.rentalcar.rentalcar.service.BookingService;
import com.rentalcar.rentalcar.service.SearchCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private CarMapper carMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private AdditionalFunctionMapper additionalFunctionMapper;
    @Autowired
    private BookingService bookingService;

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

//        if (name.trim().isEmpty()) {
//            model.addAttribute("sendLocation", "Please enter location");
//
//        } else
        List<Long> listRide = new ArrayList<>();
        for (Car car : list.getContent()) {
            listRide.add(searchCarService.countBookingsByCarId((long)car.getCarId()));
        }
        List<String> rate = new ArrayList<>();

// Iterate over each Car
        for (Car car : list.getContent()) {
            // Get all BookingCar entries for the current Car
            List<BookingCar> bookingCars = searchCarService.getBookingCarByCarId((long) car.getCarId());

            double totalRating = 0.0;
            int totalCount = 0;

            // For each BookingCar, get the feedback and calculate total rating
            for (BookingCar bookingCar : bookingCars) {
                List<Feedback> feedbackList = searchCarService.getFeedbackByBookingId((long) bookingCar.getBookingId());

                // Calculate the sum of ratings for this BookingCar
                if (!feedbackList.isEmpty()) {
                    for (Feedback feedback : feedbackList) {
                        totalRating += feedback.getRating();
                        totalCount++;
                    }
                }
            }

            // Calculate the average rating for this car
            if (totalCount > 0) {
                double average = totalRating / totalCount;
                rate.add(String.format("%.1f/5.0", average)); // Add the average to the list
            } else {
                rate.add("No ratings"); // No ratings for this Car
            }
        }
        
        if (pickDate == null) {
            model.addAttribute("sendPickDate", "Please enter pick date");
        } else if (pickTime == null ) {
            model.addAttribute("sendPickTime", "Please enter pick time");
        } else if (dropDate == null) {
            model.addAttribute("sendDropDate", "Please enter drop date");
        } else if (dropTime == null) {
            model.addAttribute("sendDropTime", "Please enter drop time");
        } else if (pickDate.isEqual(dropDate)) {
            if (pickTime.isAfter(dropTime) || pickTime.equals(dropTime)) {
                model.addAttribute("sendCondition", "Drop-off time must be after pick-up time if on the same day");
            }else if (pickTime.plusHours(1).isAfter(dropTime)) {
                model.addAttribute("sendCondition", "Drop-off time must be at least 1 hour after pick-up time if on the same day");
            }
            else{

                model.addAttribute("list", list.getContent());
                model.addAttribute("totalPage", list.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("name", name);
                model.addAttribute("pickDate",pickDate);
                model.addAttribute("dropDate",dropDate);
                model.addAttribute("dropTime",dropTime);
                model.addAttribute("pickTime",pickTime);
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("size", list.getTotalElements());
                model.addAttribute("listRide", listRide);
                model.addAttribute("rate", rate);

            }
        } else if (pickDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Pick-up date must not be in the past");
        } else if (dropDate.isBefore(today)) {
            model.addAttribute("sendCondition", "Drop-off date must not be in the past");
        } else if (pickDate.isAfter(dropDate)) {
            model.addAttribute("sendCondition", "Drop-off date  must be later than Pick-up date ");
        }else {
            model.addAttribute("list", list.getContent());
            model.addAttribute("totalPage", list.getTotalPages());
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("name", name);
            model.addAttribute("pickDate",pickDate);
            model.addAttribute("dropDate",dropDate);
            model.addAttribute("dropTime",dropTime);
            model.addAttribute("pickTime",pickTime);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("size", list.getTotalElements());
            model.addAttribute("listRide", listRide);
            model.addAttribute("rate", rate);

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

    @GetMapping("/car-search")
    public String cars(
            @RequestParam(value = "pickupLocation" , required = false) String pickupLocation,
            @RequestParam(value = "pickupDateTime", required = false) String pickupDateTime,
            @RequestParam(value = "dropDateTime", required = false) String dropDateTime,
            Model model
    ) {
//        List<Integer> statusIds = List.of(1, 2, 3, 5, 6, 10);
//        List<Car> cars = carRepository.findAllByCarStatus_StatusIdIsIn(statusIds);
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("dropDateTime", dropDateTime);
//        model.addAttribute("cars", cars);
        return "products/CarSearch";
    }

    @GetMapping("/api/searchCar")
    public ResponseEntity<List<CarDto1>> searchCars(
            @RequestParam(value = "pickupLocation" , required = false) String pickupLocation,
            @RequestParam(value = "pickupDateTime", required = false) String pickupDateTime,
            @RequestParam(value = "dropDateTime", required = false) String dropDateTime,
            @RequestParam(value = "selectedBrands", required = false) Set<Integer> brandCheckList,
            @RequestParam(value = "selectedFunctions", required = false) Set<Integer> additionalFunctionCheckList,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice
    ) {
        List<Integer> statusIds = List.of(1, 2, 3, 5, 6, 10);
        List<CarDto1> cars = carRepository.findAllByCarStatus_StatusIdIsIn(statusIds).stream().map(carMapper::toDto).collect(Collectors.toList());
        cars = filterCar(cars, pickupLocation, pickupDateTime, dropDateTime, brandCheckList, additionalFunctionCheckList);
        return ResponseEntity.ok(cars);
    }


    @GetMapping("/search-car/{id}")
    public String carDetail(
            @PathVariable("id") Integer id,
            @RequestParam(value = "pickupLocation" , required = false) String pickupLocation,
            @RequestParam(value = "pickupDateTime", required = false) String pickupDateTime,
            @RequestParam(value = "dropDateTime", required = false) String dropDateTime,
            Model model
    ) {
        Car car = carRepository.getCarByCarId(id);
        if(car == null){
            return "redirect:/car-search";
        }
        model.addAttribute("car", carMapper.toDto(car));
        model.addAttribute("pickupLocation", pickupLocation);
        model.addAttribute("pickupDateTime", pickupDateTime);
        model.addAttribute("dropDateTime", dropDateTime);
        return "products/CarDetail";
    }

    @GetMapping("/api/brands")
    public ResponseEntity<List<BrandDto>> getBrands() {
        return ResponseEntity.ok(brandRepository.findAll().stream().map(brandMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/api/additionalFunctions")
    public ResponseEntity<List<AdditionalFunctionDto>> getAdditionalFunctions() {
        return ResponseEntity.ok(additionalFunctionRepository.findAll().stream().map(additionalFunctionMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/api/check-car-booked")
    public ResponseEntity<Boolean> checkCarBooked(@RequestParam("carId") Integer carId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        return ResponseEntity.ok(bookingService.checkAlreadyBookedCar(carId, user.getId()));
    }


    //Write functions under this line <3

    public List<CarDto1> filterCar(List<CarDto1> cars, String pickupLocation, String pickupDateTime, String dropDateTime, Set<Integer> brandCheckList, Set<Integer> additionalFunctionCheckList) {
        if(pickupLocation != null && !pickupLocation.isEmpty()) {
            cars = cars.stream().filter(car -> (car.getAddress().province()+" "+car.getAddress().district()+" "+car.getAddress().ward()).toLowerCase().contains(pickupLocation.toLowerCase().trim())).collect(Collectors.toList());
        }
        if(pickupDateTime != null && !pickupDateTime.isEmpty()) {
            cars = cars.stream().filter(car -> car.getCarStatus().getStatusId() == 1).collect(Collectors.toList());
        }
        if(dropDateTime != null && !dropDateTime.isEmpty()) {
            cars = cars.stream().filter(car -> car.getCarStatus().getStatusId() == 1).collect(Collectors.toList());
        }
        if(brandCheckList != null && !brandCheckList.isEmpty()) {
            cars = cars.stream().filter(car ->
                            brandCheckList
                                    .contains(car.getBrand().getBrandId()))
                    .collect(Collectors.toList());
        }
        if(additionalFunctionCheckList != null && !additionalFunctionCheckList.isEmpty()) {
            cars = cars.stream().filter(car ->
                            car.getAdditionalFunctions()
                                    .stream()
                                    .map(additionalFunctionMapper::toEntity)
                                    .map(AdditionalFunction::getFunctionId)
                                    .collect(Collectors.toSet())
                                    .containsAll(additionalFunctionCheckList))
                    .collect(Collectors.toList());
        }
        return cars;
    }
}