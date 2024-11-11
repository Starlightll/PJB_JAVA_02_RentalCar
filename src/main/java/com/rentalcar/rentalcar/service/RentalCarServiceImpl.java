package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.BookingDto;

import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.BookingStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.BookingStatusRepository;
import com.rentalcar.rentalcar.repository.RentalCarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RentalCarServiceImpl implements RentalCarService {

    @Autowired
    RentalCarRepository rentalCarRepository;

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Override
    public  Page<BookingDto> getBookings(int page, int size,  String sortBy, String order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        switch (sortBy) {
            case "newestToLatest":
                sortBy = "lastModified";
                order = "desc";
                break;
            case "latestToNewest":
                sortBy = "lastModified";
                order = "asc";
                break;
            case "priceLowToHigh":
                sortBy = "basePrice";
                order = "asc";
                break;
            case "priceHighToLow":
                sortBy = "basePrice";
                order = "desc";
                break;
            default:
                break;
        }
        List<BookingDto> bookingDtos = new ArrayList<>();
        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, sortBy);
        Pageable pageable = PageRequest.of(page-1, size, sort);
        Page<Object[]> resultsPage;
        resultsPage = rentalCarRepository.findAllWithPagination(user.getId(), pageable);

        for(Object[] result : resultsPage.getContent() ) {
            LocalDateTime startDate = ((Timestamp) result[2]).toLocalDateTime();
            LocalDateTime actualEndDate = ((Timestamp) result[5]).toLocalDateTime();

            // Tính toán số ngày giữa startDate và actualEndDate
            int numberOfDays = (int) ChronoUnit.DAYS.between(startDate, actualEndDate);

            BookingDto bookingDto = new BookingDto (
                    Long.valueOf((Integer) result[0]),
                    (String) result[1],
                    ((Timestamp) result[2]).toLocalDateTime(), //start date
                    ((Timestamp) result[3]).toLocalDateTime(), //end date
                    (String) result[4], // driverInfo
                    ((Timestamp) result[5]).toLocalDateTime(),//actualEndDate
                    ((BigDecimal) result[6]).doubleValue(), // total price
                    Long.valueOf((Integer) result[7]) , //userId
                    numberOfDays , //numberOfDays
                    (Integer) result[8], //paymentMethod
                    ((BigDecimal) result[9]).doubleValue(), // basePrice
                    ((BigDecimal) result[10]).doubleValue(), // deposit
                    (String) result[11], //bookingStatus
                    (String) result[12],
                    (String) result[13],
                    (String) result[14],
                    (String) result[15]
            );
            bookingDtos.add(bookingDto);
        }


        return new PageImpl<>(bookingDtos, pageable, resultsPage.getTotalElements());
    }

    @Override
    public boolean cancelBooking(Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to cancel booking with ID: " + bookingId);

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            // Check if the booking belongs to the user and is in a cancellable state
            if (booking.getUser().getId().equals(user.getId()) &&
                    (booking.getBookingStatus().getName().equals("Confirmed") ||
                            booking.getBookingStatus().getName().equals("Pending deposit") ||
                            booking.getBookingStatus().getName().equals("Stopped"))) {

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> cancelledStatusOptional = bookingStatusRepository.findByName("Cancelled");

                if (cancelledStatusOptional.isPresent()) {
                    BookingStatus cancelledStatus = cancelledStatusOptional.get();
                    booking.setBookingStatus(cancelledStatus); // Update the status of the booking

                    // Save the updated booking
                    rentalCarRepository.save(booking);
                    System.out.println("Booking with ID " + bookingId + " has been successfully cancelled.");
                    return true;
                } else {
                    System.out.println("Cancelled status not found.");
                }
            } else {
                System.out.println("Booking does not belong to the user or is not in a cancellable state.");
            }
        } else {
            System.out.println("Booking with ID " + bookingId + " not found.");
        }

        return false;
    }

    @Override
    public boolean confirmPickupBooking(Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Booking has been update to In-Progress: " + bookingId);

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            // Check if the booking belongs to the user and is in a cancellable state
            if (booking.getUser().getId().equals(user.getId()) &&
                                (booking.getBookingStatus().getName().equals("Confirmed") )){

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> cancelledStatusOptional = bookingStatusRepository.findByName("In-Progress");

                if (cancelledStatusOptional.isPresent()) {
                    BookingStatus cancelledStatus = cancelledStatusOptional.get();
                    booking.setBookingStatus(cancelledStatus); // Update the status of the booking

                    // Save the updated booking
                    rentalCarRepository.save(booking);
                    System.out.println("Booking with ID " + bookingId + " has been update to In-Progress.");
                    return true;
                } else {
                    System.out.println("In-Progress status not found.");
                }
            } else {
                System.out.println("Booking does not belong to the user or is not in a update state.");
            }
        } else {
            System.out.println("Booking with ID " + bookingId + " not found.");
        }

        return false;
    }




}
