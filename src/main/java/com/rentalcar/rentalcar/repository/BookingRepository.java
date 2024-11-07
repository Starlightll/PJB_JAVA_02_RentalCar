package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
