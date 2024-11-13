package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.DriverDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverDetailRepository extends JpaRepository<DriverDetail, Integer> {
}
