package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface CarOwnerService {
    Set<Car> listCars();
    List<Car> findAllWithSortDesc(String field);
    List<Car> findAllWithSortAsc(String field);
    void updateCar(Car car, MultipartFile[] files, User user, Integer carId);
    void deleteCar(int carId);
    void addCar(CarDraft carDraft, User user);
}
