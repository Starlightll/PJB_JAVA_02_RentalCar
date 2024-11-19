package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.DriverDetail;
import com.rentalcar.rentalcar.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverDetailRepository extends JpaRepository<DriverDetail, Integer> {
    @Query("SELECT dr FROM DriverDetail dr WHERE dr.booking.bookingId = :bookingId")
    Optional<DriverDetail> findDriverByBookingId(@Param("bookingId") Long bookingId);
}
