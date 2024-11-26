package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.CarDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CarDraftRepository extends JpaRepository<CarDraft, Integer> {

    CarDraft findTopByUser_IdAndCarId(Long user_id, Integer carId);
    CarDraft findTopByUser_IdAndCarIdIsNullOrderByLastModifiedDesc(Long userId);


    @Modifying
    @Transactional
    @Query("DELETE FROM CarDraft cd WHERE cd.user.id = :userId")
    void deleteDraftByUserId(@Param("userId") Long userId);

}
