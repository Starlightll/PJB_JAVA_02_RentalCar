package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {

}
