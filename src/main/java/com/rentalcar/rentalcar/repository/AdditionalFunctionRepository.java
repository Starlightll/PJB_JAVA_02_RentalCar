package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.AdditionalFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AdditionalFunctionRepository extends JpaRepository<AdditionalFunction, Integer> {

    Set<AdditionalFunction> findAllByFunctionIdIsIn(List<Integer> functionIds);

}
