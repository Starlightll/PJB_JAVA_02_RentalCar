package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;

import java.util.List;
import java.util.Set;

public interface CarOwnerService {
    Set<Car> listCars();
    List<Car> findAllWithSortDesc(String field);
    List<Car> findAllWithSortAsc(String field);
    void updateCar(Car car);
    void deleteCar(int carId);
    void saveCar(CarDraft carDraft, User user);
}
