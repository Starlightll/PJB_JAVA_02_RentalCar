package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.CarDto;
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
    void updateCar(Car car, MultipartFile[] files, User user, Integer carId, Integer carStatus);
    void deleteCar(int carId);
    void addCar(CarDraft carDraft, User user);
    List<Car> getCarsByStatus(Integer statusId);
    void setCarStatus(Integer carId, Integer statusId);
    boolean requestChangeBasicInformation(CarDraft carDraft, MultipartFile[] files, User user, Integer carId);
    Set<String> getAllLicensePlates(Long userId);
    Set<String> getAllLicensePlatesNotOwnedByUser(Long userId);
    CarDto getRatingByCarId(Long carId);



}
