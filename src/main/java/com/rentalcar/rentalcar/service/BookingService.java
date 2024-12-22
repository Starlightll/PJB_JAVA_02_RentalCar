package com.rentalcar.rentalcar.service;

public interface BookingService {

    Boolean checkAlreadyBookedCar(Integer carId, Long userId);

}
