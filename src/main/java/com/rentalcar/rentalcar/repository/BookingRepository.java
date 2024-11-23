package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT TOP (1000) \n" +
            "      b.bookingId,\n" +
            "      startDate,\n" +
            "      endDate,\n" +
            "      driverInfo,\n" +
            "      actualEndDate,\n" +
            "      totalPrice,\n" +
            "      b.userId,\n" +
            "      bookingStatusId,\n" +
            "      paymentMethodId,\n" +
            "      driverId,\n" +
            "      c.basePrice,\n" +
            "      c.deposit, \n" +
            "\t  u.wallet ,\n" +
            "\t  c.userId,\n" +
            "\t  c.name,\n" +
            "\t  c.carId\n" +

            "FROM RentalCar.dbo.Booking b\n" +
            "JOIN dbo.BookingCar bc ON bc.bookingId = b.bookingId\n" +
            "JOIN dbo.Car c ON bc.carId = c.carId\n" +
            "JOIN dbo.Users u ON c.userId = u.userId\n" +
            "WHERE b.bookingId = :bookingId",
            nativeQuery = true)
    Object[] findByBookingId(@Param("bookingId") Long bookingId);
}
