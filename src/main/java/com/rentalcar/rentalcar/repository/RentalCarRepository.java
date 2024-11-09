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
}
