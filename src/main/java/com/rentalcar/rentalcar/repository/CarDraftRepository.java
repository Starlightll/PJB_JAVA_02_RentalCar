package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarDraftRepository extends JpaRepository<CarDraft, Integer> {

    @Query("SELECT cd FROM CarDraft cd WHERE cd.user.id = :userId ORDER BY cd.lastModified DESC")
    CarDraft getDraftByLastModified(@Param("userId") Long userId);

}
