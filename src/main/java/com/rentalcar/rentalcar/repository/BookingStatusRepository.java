package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingStatusRepository extends JpaRepository<BookingStatus, Long> {
    Optional<BookingStatus> findByName(String name);
}

