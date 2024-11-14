package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.RentalCarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ViewEditBookingServiceImpl implements ViewEditBookingService{
    @Autowired
    RentalCarRepository rentalCarRepository;


    @Override
    public MyBookingDto getBookingDetail(Integer bookingId, Integer carId, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        Object[] obj = rentalCarRepository.findBookingDetail(user.getId(),carId,bookingId);
        if(obj == null || obj.length == 0){
            throw new RuntimeException("booking detail not found");
        }
        Object[] result = (Object[]) obj[0];

        LocalDateTime startDate = ((Timestamp) result[2]).toLocalDateTime();
        LocalDateTime actualEndDate = ((Timestamp) result[5]).toLocalDateTime();

        // Tính toán số ngày giữa startDate và actualEndDate
        int numberOfDays = (int) ChronoUnit.DAYS.between(startDate, actualEndDate);

        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                (String) result[1],
                ((Timestamp) result[2]).toLocalDateTime(), //start date
                ((Timestamp) result[3]).toLocalDateTime(), //end date
                (String) result[4], // driverInfo
                ((Timestamp) result[5]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[6]).doubleValue(), // total price
                Long.valueOf((Integer) result[7]), //userId
                numberOfDays, //numberOfDays
                (Integer) result[8], //paymentMethod
                ((BigDecimal) result[9]).doubleValue(), // basePrice
                ((BigDecimal) result[10]).doubleValue(), // deposit
                (String) result[11], //bookingStatus
                (String) result[12],
                (String) result[13],
                (String) result[14],
                (String) result[15]
        );
        return bookingDto;
    }
}
