package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CarOwnerServiceImpl implements CarOwnerService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public Set<Car> listCars() {
        return null;
    }

    @Override
    public Car getCarById(int carId) {
        return null;
    }

    @Override
    public void addCar(Car car) {

    }

    @Override
    public void updateCar(Car car) {

    }

    @Override
    public void deleteCar(int carId) {

    }
}
