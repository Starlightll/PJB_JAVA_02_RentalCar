package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> getCarsByUser(User user);

    Car getCarByCarId(Integer carId);

    Page<Car> findAllByUser(User user, Pageable pageable);

    @Query(value = "SELECT TOP 1 licensePlate FROM Car WHERE licensePlate = :licensePlate", nativeQuery = true)
    String findFirstLicensePlateMatchNative(@Param("licensePlate") String licensePlate);

    //Select first user from car where licensePlate = :licensePlate
    @Query(value = "SELECT TOP 1 Car.userId FROM Car WHERE licensePlate = :licensePlate", nativeQuery = true)
    Long findFirstUserByLicensePlate(@Param("licensePlate") String licensePlate);

    @Query(value = "SELECT * FROM Car WHERE Car.statusId = :statusId AND Car.userId = :userId", nativeQuery = true)
    Page<Car> findAllByCarStatusAndUser(Integer statusId, Long userId, Pageable pageable);


    @Query(value = "SELECT c.[carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], " +
            "[transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], " +
            "[termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], " +
            "[insurance], [lastModified], [userId], [brandId], [statusId], " +
            "ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS averageRating " +
            "FROM [RentalCar].[dbo].[Car] c " +
            "LEFT JOIN [dbo].[BookingCar] bc ON bc.carId = c.carId " +
            "LEFT JOIN [dbo].[Feedback] f ON f.bookingId = bc.bookingId " +
            "WHERE c.carId = :carId " +
            "GROUP BY c.[carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], " +
            "[transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], " +
            "[termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], " +
            "[insurance], [lastModified], [userId], [brandId], [statusId]",
            nativeQuery = true)
    Object[] findCarByCarId(@Param("carId") Integer carId);

}
