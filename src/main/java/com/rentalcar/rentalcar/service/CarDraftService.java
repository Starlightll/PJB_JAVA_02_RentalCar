package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface CarDraftService {
    CarDraft getDraftByLastModified(Long userId);

    CarDraft getDraftOfRequestChangeBasicInformation(Long userId, Integer carId, String verifyStatus);

    void deleteDraftByUserId(Long userId);

    void saveDraft(CarDraft carDraft,  MultipartFile[] files, User user);

    void saveRequestChangeBasicInformation(CarDraft carDraft, MultipartFile[] files, User user);

    CarDraft createCarDraft(User user);

    Car convertCarDraftToCar(CarDraft carDraft);

    CarDraft convertCarToCarDraft(Car car);
}
