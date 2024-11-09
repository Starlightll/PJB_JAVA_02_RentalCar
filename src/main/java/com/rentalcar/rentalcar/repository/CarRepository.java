package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.entity.User;
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

    @Query(value = "SELECT c.*, cd.province, cd.provinceId, cd.district, cd.districtId, cd.ward, cd.wardId, cd.street " +
            "FROM Car c " +
            "INNER JOIN CarAddress cd ON c.carId = cd.carId " +
            "WHERE (cd.province LIKE %:province% OR cd.district LIKE %:district%)",
            nativeQuery = true)
    List<Car> findCarByCarName(@Param("province") String province, @Param("district") String district);
}
