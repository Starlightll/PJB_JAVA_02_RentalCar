package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface CarDraftService {
    CarDraft getDraftByLastModified(Long userId);

    void deleteDraftByUserId(Long userId);

    void saveDraft(CarDraft carDraft,  MultipartFile[] files, User user);

    CarDraft createCarDraft(User user);

    Car convertCarDraftToCar(CarDraft carDraft);

    CarDraft convertCarToCarDraft(Car car);
}
