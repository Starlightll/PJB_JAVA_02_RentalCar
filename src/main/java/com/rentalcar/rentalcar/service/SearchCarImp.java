package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.BookingCar;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface SearchCarImp {
    List<Car> getCarList();

    List<Car> findCarList(String name, Sort sort);

    Page<Car> findCars(String name, Integer pageNo , Sort sort);
    Page<Car> getAllCars(Integer pageNo);

   long countBookingsByCarId(Long carId);
   List<Feedback> getFeedbackByBookingId(Long carId);
   List<BookingCar> getBookingCarByCarId(Long carId);
}