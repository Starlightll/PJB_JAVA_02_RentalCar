package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;

import java.util.List;

public interface SearchCarImp {
    List<Car> getCarList();

    List<Car> findCars(String name);
}
