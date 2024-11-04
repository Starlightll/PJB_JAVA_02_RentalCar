package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarStatusRepository extends JpaRepository<CarStatus, Integer> {
}
