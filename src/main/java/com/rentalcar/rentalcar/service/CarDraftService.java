package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarDraft;

public interface CarDraftService {
    CarDraft getDraftByLastModified(Long userId);
}
