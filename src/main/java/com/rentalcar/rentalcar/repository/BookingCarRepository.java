package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.BookingCar;
import com.rentalcar.rentalcar.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingCarRepository extends JpaRepository<BookingCar, BookingCar.BookingCarId> {
    long countByCarId(Long carId);
    List<BookingCar> getByCarId(Long carId);
}
