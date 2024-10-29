package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;

public interface CarDraftService {
    CarDraft getDraftByLastModified(Long userId);

    void deleteDraftByUserId(Long userId);

    void saveDraft(CarDraft carDraft, User user);
}
