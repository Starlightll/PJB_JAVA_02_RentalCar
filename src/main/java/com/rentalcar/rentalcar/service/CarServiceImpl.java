package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.CarStatus;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    public Integer getCurrentCarStatus(Integer carId) {

        /*
         0,Available
         1,Pending deposit
         2,Confirmed
         3,In-Progress
         4,Pending payment
         5,Completed
         6,Cancelled
         7,Pending cancel
         8,Pending return
         */

        List<Booking> booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 1);
        if (!booking.isEmpty()) {
            return 1;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 2);
        if (!booking.isEmpty()) {
            return 2;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 3);
        if (!booking.isEmpty()) {
            return 3;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 4);
        if (!booking.isEmpty()) {
            return 4;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 5);
        if (!booking.isEmpty()) {
            return 5;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 6);
        if (!booking.isEmpty()) {
            return 6;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 7);
        if (!booking.isEmpty()) {
            return 7;
        }
        booking = carRepository.findBookingByCarIdAndBookingStatusId(carId, 8);
        if (!booking.isEmpty()) {
            return 8;
        }
        return 0;
    }
}
