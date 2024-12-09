package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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


    @Query(value="SELECT b.[bookingId]\n" +
            "      ,[startDate]\n" +
            "      ,[endDate]\n" +
            "      ,[driverInfo]\n" +
            "      ,[actualEndDate]\n" +
            "      ,[totalPrice]\n" +
            "      ,b.[userId]\n" +
            "      ,[bookingStatusId]\n" +
            "      ,[paymentMethodId]\n" +
            "      ,[driverId] \n" +
            "\t  ,c.basePrice\n" +
            "\t  ,c.deposit\n" +
            "\t  ,u.wallet\n" +
            "\t  ,c.userId\n" +
            "\t  ,c.name\n" +
            "\t  ,c.carId\n" +
            "  FROM [RentalCar].[dbo].[Booking] b\n" +
            "  JOIN [dbo].[BookingCar] bc ON bc.bookingId = b.bookingId\n" +
            "  JOIN [dbo].[Car] c ON c.carId = bc.carId\n" +
            "  JOIN [dbo].[Users] u ON u.userId = b.userId\n" +
            "  WHERE b.endDate BETWEEN :startOfDay and :endOfDay",
            nativeQuery = true)
    List<Object[]> findByEndDateBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b JOIN b.bookingCars bc JOIN bc.car c WHERE c.user.id = :userId AND b.endDate >= :startDate AND b.endDate < :endDate")
    Double calculateRevenueByCarOwnerAndDate(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(b.bookingId) " +
            "FROM RentalCar.dbo.Booking b " +
            "WHERE YEAR(b.startDate) = :year AND MONTH(b.startDate) = :month",
            nativeQuery = true)
    int countBookingsInMonth(@Param("year") int year, @Param("month") int month);


}
