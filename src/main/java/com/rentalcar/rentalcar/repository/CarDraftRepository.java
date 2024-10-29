package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CarDraftRepository extends JpaRepository<CarDraft, Integer> {

    CarDraft findTopByUser_IdOrderByLastModifiedDesc(Long userId);

    //Write me deleteDraftByUserId
    @Modifying
    @Transactional
    @Query("DELETE FROM CarDraft cd WHERE cd.user.id = :userId")
    void deleteDraftByUserId(@Param("userId") Long userId);

}
