package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCarService implements SearchCarImp{
    @Autowired
    private CarRepository carRepository;
    @Override
    public List<Car> getCarList() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> findCars(String name) {
        return carRepository.findCarByCarName(name,name);
    }
}
