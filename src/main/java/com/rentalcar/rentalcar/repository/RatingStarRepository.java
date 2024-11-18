package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.dto.FeedbackDto;
import com.rentalcar.rentalcar.entity.Feedback;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingStarRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f WHERE f.booking.bookingId = :bookingId")
    Optional<Feedback> findByBookingId(@Param("bookingId") Long bookingId);


    @Query(value = "SELECT ROUND(AVG(CAST(f.rating AS FLOAT)), 2) AS average_rating " +
            "FROM Feedback f " +
            "JOIN Booking b ON f.bookingId = b.bookingId " +
            "JOIN BookingCar bc ON b.bookingId = bc.bookingId " +
            "JOIN Car c ON bc.carId = c.carId " +
            "WHERE c.userId = :userId", nativeQuery = true)
    Double getAvgRatingByUserId(@Param("userId") Long userId);


    @Query(value = "SELECT f.rating, COUNT(*) AS count\n" +
            "FROM [dbo].[Feedback] f\n" +
            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId\n" +
            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId\n" +
            "JOIN [dbo].[Car] c ON bc.carId = c.carId\n" +
            "WHERE c.userId =:userId\n" +
            "GROUP BY f.rating\n", nativeQuery = true)
    List<Object[]> getRatingCountByUserId(@Param("userId") Long userId);


//    @Query(value = "SELECT f.content, f.dateTime, f.rating, f.bookingId, b.actualEndDate, b.startDate, b.userId, c.front, c.name, u.username\n" +
//            "FROM [dbo].[Feedback] f\n" +
//            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId\n" +
//            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId\n" +
//            "JOIN [dbo].[Car] c ON bc.carId = c.carId\n" +
//            "JOIN [dbo].[Users] u ON b.userId = u.userId\n" +
//            "WHERE c.userId = :userId AND f.rating = :rating", nativeQuery = true)
//    List<Object[]> findByRating(@Param("userId") Long userId, @Param("rating") Integer rating);
//
//    @Query(value = "SELECT f.content, f.dateTime, f.rating, f.bookingId, b.actualEndDate, b.startDate, b.userId, c.front, c.name, u.username\n" +
//            "FROM [dbo].[Feedback] f\n" +
//            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId\n" +
//            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId\n" +
//            "JOIN [dbo].[Car] c ON bc.carId = c.carId\n" +
//            "JOIN [dbo].[Users] u ON b.userId = u.userId\n" +
//            "WHERE c.userId = :userId ", nativeQuery = true)
//    List<Object[]> findAll(@Param("userId") Long userId);

    @Query(value = "SELECT f.content, f.dateTime, f.rating, f.bookingId, b.actualEndDate, b.startDate, b.userId, c.front, c.name, u.username " +
            "FROM [dbo].[Feedback] f " +
            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId " +
            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId " +
            "JOIN [dbo].[Car] c ON bc.carId = c.carId " +
            "JOIN [dbo].[Users] u ON b.userId = u.userId " +
            "WHERE c.userId = :userId AND f.rating = :rating",
            countQuery = "SELECT COUNT(*) FROM [dbo].[Feedback] f " +
                    "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId " +
                    "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId " +
                    "JOIN [dbo].[Car] c ON bc.carId = c.carId " +
                    "JOIN [dbo].[Users] u ON b.userId = u.userId " +
                    "WHERE c.userId = :userId AND f.rating = :rating",
            nativeQuery = true)
    Page<Object[]> findByRatingWithPagination(@Param("userId") Long userId, @Param("rating") Integer rating, Pageable pageable);

    @Query(value = "SELECT f.content, f.dateTime, f.rating, f.bookingId, b.actualEndDate, b.startDate, b.userId, c.front, c.name, u.username " +
            "FROM [dbo].[Feedback] f " +
            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId " +
            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId " +
            "JOIN [dbo].[Car] c ON bc.carId = c.carId " +
            "JOIN [dbo].[Users] u ON b.userId = u.userId " +
            "WHERE c.userId = :userId",
            countQuery = "SELECT COUNT(*) FROM [dbo].[Feedback] f " +
                    "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId " +
                    "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId " +
                    "JOIN [dbo].[Car] c ON bc.carId = c.carId " +
                    "JOIN [dbo].[Users] u ON b.userId = u.userId " +
                    "WHERE c.userId = :userId",
            nativeQuery = true)
    Page<Object[]> findAllWithPagination(@Param("userId") Long userId, Pageable pageable);


    @Query(value = "SELECT COUNT(*) " +
            "FROM [dbo].[Feedback] f\n" +
            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId\n" +
            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId\n" +
            "JOIN [dbo].[Car] c ON bc.carId = c.carId\n" +
            "WHERE c.userId = :userId AND f.rating = :rating", nativeQuery = true)
    long countByRating(@Param("userId") Long userId, @Param("rating") int rating);

    @Query(value = "SELECT COUNT(*)" +
            "FROM [dbo].[Feedback] f\n" +
            "JOIN [dbo].[Booking] b ON f.bookingId = b.bookingId\n" +
            "JOIN [dbo].[BookingCar] bc ON b.bookingId = bc.bookingId\n" +
            "JOIN [dbo].[Car] c ON bc.carId = c.carId\n" +
            "WHERE c.userId = :userId", nativeQuery = true)
    long countByUser(@Param("userId") Long userId);



}
