package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarStatusRepository extends JpaRepository<CarStatus, Integer> {
    Optional<CarStatus> findByName(String name);
}
