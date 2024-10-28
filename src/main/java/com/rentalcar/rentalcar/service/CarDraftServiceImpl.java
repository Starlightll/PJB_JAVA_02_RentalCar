package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDraftServiceImpl implements CarDraftService {

    @Autowired
    private CarDraftRepository carDraftRepository;

    @Override
    public CarDraft getDraftByLastModified(Long userId) {
        return carDraftRepository.getDraftByLastModified(userId);
    }
}
