package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;

import java.util.Set;

public interface CarOwnerService {
    Set<Car> listCars();
    Car getCarById(int carId);
    void addCar(Car car);
    void updateCar(Car car);
    void deleteCar(int carId);
}
