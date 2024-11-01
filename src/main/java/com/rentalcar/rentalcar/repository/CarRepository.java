package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> getCarsByUser(User user);

    Car getCarByCarId(Integer carId);
}
