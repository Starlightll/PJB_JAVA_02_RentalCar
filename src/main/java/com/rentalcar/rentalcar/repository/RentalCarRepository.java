package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalCarRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b.[bookingId]\n" +
            "\t  ,c.name\n" +
            "      ,[startDate]\n" +
            "      ,[endDate]\n" +
            "      ,[driverInfo]\n" +
            "      ,[actualEndDate]\n" +
            "      ,[totalPrice]\n" +
            "      ,b.[userId]\n" +
            "      ,[paymentMethodId]\n" +
            "\t  ,c.basePrice\n" +
            "\t  ,c.deposit\n" +
            "\t  ,bs.name\n" +
            "\t  ,c.front\n" +
            "\t  ,c.back\n" +
            "\t  ,c.[left]\n" +
            "\t  ,c.[right]\n" +
            "  FROM [RentalCar].[dbo].[Booking] b\n" +
            "  Join [dbo].[Users] u On u.userId = b.userId\n" +
            "  Join [dbo].[BookingCar] bc on bc.bookingId = b.bookingId\n" +
            "  Join [dbo].[Car] c On c.carId = bc.carId\n" +
            "  Join [dbo].[BookingStatus] bs On bs.BookingStatusId = b.bookingStatusId\n" +
            "  where b.userId = :userId ",
            countQuery = "  SELECT COUNT(*)\n" +
                    "    FROM [RentalCar].[dbo].[Booking] b\n" +
                    "  Join [dbo].[Users] u On u.userId = b.userId\n" +
                    "  Join [dbo].[BookingCar] bc on bc.bookingId = b.bookingId\n" +
                    "  Join [dbo].[Car] c On c.carId = bc.carId\n" +
                    "  Join [dbo].[BookingStatus] bs On bs.BookingStatusId = b.bookingStatusId\n" +
                    "  where b.userId = :userId ",
            nativeQuery = true)
    Page<Object[]> findAllWithPagination(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "  SELECT b.bookingId,\n" +
            "       c.name,\n" +
            "       b.startDate,\n" +
            "       b.endDate,\n" +
            "       b.driverInfo,\n" +
            "       b.actualEndDate,\n" +
            "       b.totalPrice,\n" +
            "       b.userId,\n" +
            "       b.paymentMethodId,\n" +
            "       c.basePrice,\n" +
            "       c.deposit,\n" +
            "       bs.name AS bookingStatusName,\n" +
            "       c.front,\n" +
            "       c.back,\n" +
            "       c.[left],\n" +
            "       c.[right]\n" +
            "FROM RentalCar.dbo.Booking b\n" +
            "JOIN dbo.Users u ON u.userId = b.userId\n" +
            "JOIN dbo.BookingCar bc ON bc.bookingId = b.bookingId\n" +
            "JOIN dbo.Car c ON c.carId = bc.carId\n" +
            "JOIN dbo.BookingStatus bs ON bs.BookingStatusId = b.bookingStatusId\n" +
            "WHERE b.userId = :userId and c.carId = :carId and b.bookingId = :bookingId;",
            nativeQuery = true)
    Object[] findBookingDetail(@Param("userId") Long userId,@Param("carId") Integer carId,@Param("bookingId") Integer bookingId);
}
