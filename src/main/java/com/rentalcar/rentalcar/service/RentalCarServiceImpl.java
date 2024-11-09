package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.BookingDto;

import com.rentalcar.rentalcar.entity.User;
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

@Service
public class RentalCarServiceImpl implements RentalCarService {

    @Autowired
    RentalCarRepository rentalCarRepository;

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
}
