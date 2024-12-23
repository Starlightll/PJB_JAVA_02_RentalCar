package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarStatus;

public interface CarService {
    Integer getCurrentCarStatus(Integer carId);
}
