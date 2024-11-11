package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SearchCarImp {
    List<Car> getCarList();

    List<Car> findCarList(String name, Sort sort);

    Page<Car> findCars(String name, Integer pageNo , Sort sort);
    Page<Car> getAllCars(Integer pageNo);


}