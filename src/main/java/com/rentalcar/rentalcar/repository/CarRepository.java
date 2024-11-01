package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> getCarsByUser(User user);

    Car getCarByCarId(Integer carId);


    @Query(value = "SELECT TOP 1 licensePlate FROM Car WHERE licensePlate = :licensePlate", nativeQuery = true)
    String findFirstLicensePlateMatchNative(@Param("licensePlate") String licensePlate);

}
