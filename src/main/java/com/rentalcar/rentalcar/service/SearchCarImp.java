package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchCarImp {
    List<Car> getCarList();

    List<Car> findCars(String name);

    Page<Car> findCars(String name, Integer pageNo);
    Page<Car> getAllCars(Integer pageNo);

}