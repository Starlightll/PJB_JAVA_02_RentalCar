package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.RentalCarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import static com.rentalcar.rentalcar.common.Constants.*;


@Service
public class ReturnCarServiceImpl implements ReturnCarService {
    @Autowired
    RentalCarRepository rentalCarRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Override
    public String returnCar(Long bookingId, HttpSession session) {
//        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), //start date
                ((Timestamp) result[2]).toLocalDateTime(), //end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), //userId
                (Integer) result[7], //bookingStatus
                (Integer) result[8], //paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, //driver
                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue() // deposit
        );

        Double totalPrice = calculateTotalPrice(bookingId);
        if(totalPrice > bookingDto.getDeposit()){
            return String.format("Please confirm to return the car. The remaining amount of %.2f VND will be deducted from your wallet.", totalPrice - bookingDto.getDeposit());
        }
        else {
            return String.format("Please confirm to return the car. The exceeding amount of %.2f VND will be returned to your wallet..", bookingDto.getDeposit() - totalPrice );
        }

    }

    @Override
    public Double calculateTotalPrice(Long bookingId) {
        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), //start date
                ((Timestamp) result[2]).toLocalDateTime(), //end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), //userId
                (Integer) result[7], //bookingStatus
                (Integer) result[8], //paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, //driver

                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue() // deposit


        );

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();


            LocalDateTime currentDate = LocalDateTime.now();

            long numberOfDaysOverdue = ChronoUnit.DAYS.between(booking.getEndDate(), currentDate);
            if (currentDate.isAfter(booking.getEndDate())) {
                return booking.getTotalPrice();
            } else {
                return numberOfDaysOverdue * bookingDto.getBasePrice() * FINE_COST / 100 +  booking.getTotalPrice();
            }

        }
        return 0.0;
    }
}
