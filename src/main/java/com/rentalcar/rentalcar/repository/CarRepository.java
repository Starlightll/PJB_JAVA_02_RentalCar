package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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


    @Query(value = "SELECT c.[carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], " + "[transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], " + "[termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], " + "[insurance], [lastModified], [userId], [brandId], [statusId], " + "ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS averageRating " + "FROM [RentalCar].[dbo].[Car] c " + "LEFT JOIN [dbo].[BookingCar] bc ON bc.carId = c.carId " + "LEFT JOIN [dbo].[Feedback] f ON f.bookingId = bc.bookingId " + "WHERE c.carId = :carId " + "GROUP BY c.[carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], " + "[transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], " + "[termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], " + "[insurance], [lastModified], [userId], [brandId], [statusId]", nativeQuery = true)
    Object[] findCarByCarId(@Param("carId") Integer carId);

    @Query(value = "SELECT c.[carId], ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS averageRating FROM [RentalCar].[dbo].[Car] c LEFT JOIN [dbo].[BookingCar] bc ON bc.carId = c.carId LEFT JOIN [dbo].[Feedback] f ON f.bookingId = bc.bookingId WHERE c.carId = :carId GROUP BY c.[carId]", nativeQuery = true)
    Object[] getRatingByCarId(@Param("carId") Long carId);

    @Query(value = "SELECT c.*, cd.province, cd.provinceId, cd.district, cd.districtId, cd.ward, cd.wardId, cd.street " + "FROM Car c " + "INNER JOIN CarAddress cd ON c.carId = cd.carId " + "WHERE (cd.province LIKE %:province% OR cd.district LIKE %:district%)", nativeQuery = true)
    List<Car> findCarByCarName(@Param("province") String province, @Param("district") String district, Sort sort);


    @Query(value = "SELECT TOP (1) c.carId, c.name, c.licensePlate, c.model, c.color, " + "c.seatNo, c.productionYear, c.transmission, c.fuel, c.mileage, " + "c.fuelConsumption, c.basePrice, c.deposit, c.description, c.termOfUse, " + "c.carPrice, c.front, c.back, [left], [right], c.registration, " + "c.certificate, c.insurance, c.lastModified, c.userId, c.brandId, " + "c.statusId, b.bookingStatusId, bs.name, b.bookingId " + "FROM RentalCar.dbo.Car c " + "JOIN dbo.BookingCar bc ON c.carId = bc.carId " + "JOIN dbo.Booking b ON bc.bookingId = b.bookingId " + "JOIN dbo.BookingStatus bs ON b.bookingStatusId = bs.BookingStatusId " + "WHERE c.carId = :carId AND b.bookingStatusId = :bookingStatusId", nativeQuery = true)
    Object[] findCarAndBookingByCarId(@Param("carId") Long carId, @Param("bookingStatusId") Integer bookingStatusId);


    @Query(value = "SELECT TOP (1000) b.bookingStatusId\n" + "  FROM [RentalCar].[dbo].[Car] c\n" + "  JOIN [dbo].[BookingCar] bc ON c.carId = bc.carId\n" + "  JOIN [dbo].[Booking] b ON bc.bookingId = b.bookingId\n" + "  WHERE c.carId = :carId", nativeQuery = true)
    List<Integer> findBookingStatusIdByCarId(@Param("carId") Integer carId);

    @Query(value = "SELECT * FROM Car WHERE Car.statusId = :statusId", nativeQuery = true)
    List<Car> findAllByCarStatus(Integer statusId);

    List<Car> findAllByCarStatus_StatusIdIsIn(Collection<Integer> carStatusStatusIds);


    Page<Car> findAllByCarStatus_StatusIdInAndUserId(Collection<Integer> carStatusStatusIds, Long userId, Pageable pageable);

    @Query(value = "SELECT licensePlate FROM Car WHERE userId = :userId GROUP BY licensePlate", nativeQuery = true)
    Set<String> findAllLicensePlateByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT licensePlate FROM Car WHERE userId != :userId GROUP BY licensePlate", nativeQuery = true)
    Set<String> findAllLicensePlateNotOwnedByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT TOP 5 \n" +
            "    c.carId, \n" +
            "    c.name,  \n" +
            "    c.model, \n" +
            "    c.basePrice, \n" +
            "    c.front, \n" +
            "    c.back, \n" +
            "    c.[left],  \n" +
            "    c.[right], \n" +
            "    ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS averageRating \n" +
            "FROM [RentalCar].[dbo].[Car] c\n" +
            "LEFT JOIN [dbo].[BookingCar] bc ON bc.carId = c.carId\n" +
            "LEFT JOIN [dbo].[Feedback] f ON f.bookingId = bc.bookingId\n" +
            "WHERE c.statusId = 1\n" +
            "GROUP BY c.carId, c.name, c.model, c.basePrice, c.front, c.back,  c.[left],  c.[right]\n" +
            "ORDER BY averageRating DESC", nativeQuery = true)
    List<Object[]> findTop5CarsWithHighestRating();

    //Get rateAvg of car by carId
    @Query(value = "SELECT ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS averageRating FROM [RentalCar].[dbo].[Car] c LEFT JOIN [dbo].[BookingCar] bc ON bc.carId = c.carId LEFT JOIN [dbo].[Feedback] f ON f.bookingId = bc.bookingId WHERE c.carId = :carId GROUP BY c.carId", nativeQuery = true)
    Double getRateAvgByCarId(@Param("carId") Integer carId);

    //Get number of completed bookings by carId
    @Query("SELECT COUNT(b) FROM Booking b JOIN b.bookingCars bc WHERE b.bookingStatus.name = 'Completed' AND bc.car.carId = :carId")
    long countCompletedBookingsByCarId(@Param("carId") Integer carId);

}
