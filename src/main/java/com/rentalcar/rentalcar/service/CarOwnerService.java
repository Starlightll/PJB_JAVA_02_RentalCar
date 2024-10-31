package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;

import java.util.Set;

public interface CarOwnerService {
    Set<Car> listCars();
    Car getCarById(int carId);
    void addCar(Car car);
    void updateCar(Car car);
    void deleteCar(int carId);
    void saveCar(CarDraft carDraft, User user);
}
