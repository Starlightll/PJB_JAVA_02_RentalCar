package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.BookingCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingCarRepository extends JpaRepository<BookingCar, BookingCar.BookingCarId> {
}
