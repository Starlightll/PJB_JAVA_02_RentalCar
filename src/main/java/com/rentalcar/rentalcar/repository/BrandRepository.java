package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
