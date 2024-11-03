package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.CarDraft;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class CarDraftServiceImpl implements CarDraftService {

    @Autowired
    private CarDraftRepository carDraftRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public CarDraft getDraftByLastModified(Long userId) {
        return carDraftRepository.findTopByUser_IdOrderByLastModifiedDesc(userId);
    }

    @Override
    public void deleteDraftByUserId(Long userId) {
        //Delete all files in the folder
        CarDraft carDraft = getDraftByLastModified(userId);
        if (carDraft != null) {
            String folderName = String.format("%s", carDraft.getDraftId()+"");
            Path draftFolderPath = Paths.get("uploads/CarOwner/"+userId+"/Draft/", folderName);
            fileStorageService.deleteFolder(draftFolderPath);
        }
        carDraftRepository.deleteDraftByUserId(userId);
    }

    @Override
    public void saveDraft(CarDraft draft, MultipartFile[] files, User user) {
        CarDraft carDraft = getDraftByLastModified(user.getId());
        String draftId = carDraft != null ? carDraft.getDraftId().toString() : UUID.randomUUID().toString();

        // Create folder path
        String folderName = String.format("%s", draftId);
        Path draftFolderPath = Paths.get("uploads/CarOwner/"+user.getId()+"/Draft/", folderName);

        try {

            // Store each file if it exists
            //Front image
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                files[0].getSize();
                String storedPath = fileStorageService.storeFile(files[0], draftFolderPath, "frontImage."+getExtension(files[0].getOriginalFilename()));
                carDraft.setFrontImage(storedPath);
            }
            //Back image
            if (files[1] != null && !files[1].isEmpty() && files[1].getSize() > 0) {
                files[1].getSize();
                String storedPath = fileStorageService.storeFile(files[1], draftFolderPath, "backImage."+getExtension(files[1].getOriginalFilename()));
                carDraft.setBackImage(storedPath);
            }
            //Left image
            if (files[2] != null && !files[2].isEmpty() && files[2].getSize() > 0) {
                files[2].getSize();
                String storedPath = fileStorageService.storeFile(files[2], draftFolderPath, "leftImage."+getExtension(files[2].getOriginalFilename()));
                carDraft.setLeftImage(storedPath);
            }
            //Right image
            if (files[3] != null && !files[3].isEmpty() && files[3].getSize() > 0) {
                files[3].getSize();
                String storedPath = fileStorageService.storeFile(files[3], draftFolderPath, "rightImage."+getExtension(files[3].getOriginalFilename()));
                carDraft.setRightImage(storedPath);
            }
            //Registration
            if (files[4] != null && !files[4].isEmpty() && files[4].getSize() > 0) {
                files[4].getSize();
                String storedPath = fileStorageService.storeFile(files[4], draftFolderPath, "registration."+getExtension(files[4].getOriginalFilename()));
                carDraft.setRegistration(storedPath);
            }
            //Certificate
            if (files[5] != null && !files[5].isEmpty() && files[5].getSize() > 0) {
                files[5].getSize();
                String storedPath = fileStorageService.storeFile(files[5], draftFolderPath, "certificate."+getExtension(files[5].getOriginalFilename()));
                carDraft.setCertificate(storedPath);
            }
            //Insurance
            if (files[6] != null && !files[6].isEmpty() && files[6].getSize() > 0) {
                String storedPath = fileStorageService.storeFile(files[6], draftFolderPath, "insurance."+getExtension(files[6].getOriginalFilename()));
                carDraft.setInsurance(storedPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(carDraft != null) {
            carDraft.setStep(draft.getStep());
            carDraft.setLicensePlate(draft.getLicensePlate().toUpperCase().trim());
            carDraft.setLastModified(draft.getLastModified());
            carDraft.setModel(draft.getModel().trim());
            carDraft.setColor(draft.getColor().trim());
            carDraft.setSeat(draft.getSeat());
            carDraft.setProductionYear(draft.getProductionYear());
            carDraft.setTransmission(draft.getTransmission().trim());
            carDraft.setFuelType(draft.getFuelType().trim());
            carDraft.setMileage(draft.getMileage());
            carDraft.setFuelConsumption(draft.getFuelConsumption());
            carDraft.setAdditionalFunction(draft.getAdditionalFunction().trim());
            carDraft.setProvince(draft.getProvince().trim());
            carDraft.setDistrict(draft.getDistrict().trim());
            carDraft.setWard(draft.getWard().trim());
            carDraft.setHome(draft.getHome().trim());
            carDraft.setBasePrice(draft.getBasePrice());
            carDraft.setDeposit(draft.getDeposit());
            carDraft.setDescription(draft.getDescription().trim());
            carDraft.setTerms(draft.getTerms().trim());
            carDraft.setCarPrice(draft.getCarPrice());
            carDraft.setBrand(draft.getBrand());
        } else {
            carDraft = draft;
            carDraft.setUser(user);
        }
        carDraftRepository.save(carDraft);
    }

    @Override
    public CarDraft createCarDraft(User user) {
        CarDraft carDraft = getDraftByLastModified(user.getId());
        if (carDraft != null) {
            return carDraft;
        } else {
            carDraftRepository.deleteDraftByUserId(user.getId());
            carDraft = new CarDraft();
            carDraft.setUser(user);
            carDraft.setLastModified(new Date());
            carDraftRepository.save(carDraft);
            return carDraft;
        }
    }


}
