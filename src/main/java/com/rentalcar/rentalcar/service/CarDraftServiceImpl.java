package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarDraftServiceImpl implements CarDraftService {

    @Autowired
    private CarDraftRepository carDraftRepository;

    @Override
    public CarDraft getDraftByLastModified(Long userId) {
        return carDraftRepository.findTopByUser_IdOrderByLastModifiedDesc(userId);
    }

    @Override
    public void deleteDraftByUserId(Long userId) {
        carDraftRepository.deleteDraftByUserId(userId);
    }

    @Override
    public void saveDraft(CarDraft draft, User user) {
        CarDraft carDraft = carDraftRepository.findTopByUser_IdOrderByLastModifiedDesc(user.getId());
        if(carDraft != null) {
            carDraft.setStep(draft.getStep());
            carDraft.setLicensePlate(draft.getLicensePlate());
            carDraft.setLastModified(draft.getLastModified());
            carDraft.setModel(draft.getModel());
            carDraft.setColor(draft.getColor());
            carDraft.setSeat(draft.getSeat());
            carDraft.setProductionYear(draft.getProductionYear());
            carDraft.setTransmission(draft.getTransmission());
            carDraft.setFuelType(draft.getFuelType());
            carDraft.setMileage(draft.getMileage());
            carDraft.setFuelConsumption(draft.getFuelConsumption());
            carDraft.setAdditionalFunction(draft.getAdditionalFunction());
            carDraft.setProvince(draft.getProvince());
            carDraft.setDistrict(draft.getDistrict());
            carDraft.setWard(draft.getWard());
            carDraft.setHome(draft.getHome());
            carDraft.setBasePrice(draft.getBasePrice());
            carDraft.setDeposit(draft.getDeposit());
            carDraft.setDescription(draft.getDescription());
            carDraft.setTerms(draft.getTerms());
            carDraft.setCarPrice(draft.getCarPrice());
            carDraft.setFrontImage(draft.getFrontImage());
            carDraft.setBackImage(draft.getBackImage());
            carDraft.setLeftImage(draft.getLeftImage());
            carDraft.setRightImage(draft.getRightImage());
            carDraft.setRegistration(draft.getRegistration());
            carDraft.setCertificate(draft.getCertificate());
            carDraft.setInsurance(draft.getInsurance());
            carDraft.setBrand(draft.getBrand());
        } else {
            carDraft = draft;
            carDraft.setUser(user);
        }
        carDraftRepository.save(carDraft);
    }


}
