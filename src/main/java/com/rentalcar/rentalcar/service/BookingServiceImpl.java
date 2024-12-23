package com.rentalcar.rentalcar.service;


import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.BookingStatusRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import com.rentalcar.rentalcar.repository.CarStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    private MyWalletService myWalletService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarStatusRepository carStatusRepository;

    @Override
    public Boolean checkAlreadyBookedCar(Integer carId, Long userId) {
        List<Long> bookingStatusIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<Booking> bookings = bookingRepository.findBookingsByCar_CarIdAndUser_IdAndBookingStatus_BookingStatusIdIsIn(carId, userId, bookingStatusIds);
        return !bookings.isEmpty();
    }

    @Override
    public Boolean checkOverdueBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found"));
        return booking.getEndDate().isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void cancelBooking(Booking booking){
        if(booking.getBookingStatus().getBookingStatusId() == 1){
            BookingStatus bookingStatus = bookingStatusRepository.findById(6L).orElseThrow(() -> new RuntimeException("BookingStatus not found"));
            booking.setBookingStatus(bookingStatus);
            booking.setLastModified(new Date());
            bookingRepository.save(booking);
            BigDecimal deposit = BigDecimal.valueOf(booking.getDeposit());
            //transfer deposit from admin to user
            myWalletService.transfer(1L, booking.getUser().getId(), TransactionType.REFUND_DEPOSIT, TransactionType.RECEIVE_DEPOSIT, deposit, "Admin refund deposit");
        }
        if(booking.getBookingStatus().getBookingStatusId() == 2){
            BookingStatus bookingStatus = bookingStatusRepository.findById(7L).orElseThrow(() -> new RuntimeException("BookingStatus not found"));
            booking.setBookingStatus(bookingStatus);
            booking.setLastModified(new Date());
            bookingRepository.save(booking);
            BigDecimal deposit = BigDecimal.valueOf(booking.getDeposit());
            //transfer deposit from admin to car owner
            myWalletService.transfer(1L, booking.getCarOwner().getId(), TransactionType.REFUND_DEPOSIT, TransactionType.RECEIVE_DEPOSIT, deposit, "Admin refund deposit");
        }
    }

    @Override
    @Transactional
    public void confirmBooking(Booking booking) {
        if(booking.getBookingStatus().getBookingStatusId() == 1){
            BookingStatus bookingStatus = bookingStatusRepository.findById(2L).orElseThrow(() -> new RuntimeException("BookingStatus not found"));
            //Set booking status to confirmed
            booking.setBookingStatus(bookingStatus);
            booking.setLastModified(new Date());
            bookingRepository.save(booking);
            Car car = carRepository.getCarByCarId(booking.getCar().getCarId());
            CarStatus carStatus = carStatusRepository.findCarStatusByStatusId(2);
            car.setCarStatus(carStatus);
            carRepository.save(car);
            //Cancel all waiting booking of car of user except this booking id
            cancelAllWaitingBookingOfCarOfUser(booking.getBookingId(), booking.getCar().getCarId(), booking.getUser().getId());
            BigDecimal deposit = BigDecimal.valueOf(booking.getDeposit());
            //Transfer deposit from admin to car owner
            myWalletService.transfer(1L, booking.getCarOwner().getId(), TransactionType.REFUND_DEPOSIT, TransactionType.RECEIVE_DEPOSIT, deposit, "Admin sends deposit");
        }
    }

    @Override
    @Transactional
    public void cancelAllWaitingBookingOfCarOfUser(Long bookingId, Integer carId, Long userId) {
        Set<Booking> bookings = bookingRepository.findAllByBookingStatus_BookingStatusIdAndCar_CarId(userId, carId);
        for (Booking booking : bookings) {
            if (!Objects.equals(booking.getBookingId(), bookingId)) {
                cancelBooking(booking);
            }
        }


    }

    @Override
    @Transactional
    public Integer cancelExpiredBooking() {
        Integer count = 0;
        List<Booking> bookings = bookingRepository.findExpiredBookings(LocalDateTime.now());
        for (Booking booking : bookings) {
            if(booking.getEndDate().isBefore(LocalDateTime.now())){
                cancelBooking(booking);
                count++;
            }
        }
        return count;
    }
}
