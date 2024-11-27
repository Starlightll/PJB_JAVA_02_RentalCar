package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class CarDraftServiceImpl implements CarDraftService {

    @Autowired
    private CarDraftRepository carDraftRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public CarDraft getDraftByLastModified(Long userId) {
        return carDraftRepository.findTopByUser_IdAndCarIdIsNullOrderByLastModifiedDesc(userId);
    }

    @Override
    public CarDraft getDraftOfRequestChangeBasicInformation(Long userId, Integer carId, String verifyStatus) {
        return carDraftRepository.findTopByUser_IdAndCarIdAndVerifyStatusOrderByLastModifiedDesc(userId, carId, verifyStatus);
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
            carDraft.setDescription(draft.getDescription().trim());
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
            carDraft.setCarId(draft.getCarId());
            carDraft.setVerifyStatus(draft.getVerifyStatus());
            String carName = brandRepository.findByBrandId(carDraft.getBrand().getBrandId()).getBrandName() + " " + carDraft.getModel() + " " + carDraft.getProductionYear();
            carDraft.setCarName(carName);
        } else {
            carDraft = draft;
            carDraft.setUser(user);
        }
        carDraftRepository.save(carDraft);
    }

    @Override
    public void saveRequestChangeBasicInformation(CarDraft draft, MultipartFile[] files, User user) {
        CarDraft carDraft = carDraftRepository.save(draft);
        String draftId = carDraft != null ? carDraft.getDraftId().toString() : UUID.randomUUID().toString();

        // Create folder path
        String folderName = String.format("%s", draftId);
        Path draftFolderPath = Paths.get("uploads/CarOwner/"+user.getId()+"/Draft/", folderName);

        try {
            // Store each file if it exists
            //Registration
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                String storedPath = fileStorageService.storeFile(files[0], draftFolderPath, "registration."+getExtension(files[0].getOriginalFilename()));
                carDraft.setRegistration(storedPath);
            }
            //Certificate
            if (files[1] != null && !files[1].isEmpty() && files[1].getSize() > 0) {
                String storedPath = fileStorageService.storeFile(files[1], draftFolderPath, "certificate."+getExtension(files[1].getOriginalFilename()));
                carDraft.setCertificate(storedPath);
            }
            //Insurance
            if (files[2] != null && !files[2].isEmpty() && files[2].getSize() > 0) {
                String storedPath = fileStorageService.storeFile(files[2], draftFolderPath, "insurance."+getExtension(files[2].getOriginalFilename()));
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
            carDraft.setDescription(draft.getDescription().trim());
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
            carDraft.setCarId(draft.getCarId());
            carDraft.setVerifyStatus(draft.getVerifyStatus());
            String carName = brandRepository.findByBrandId(carDraft.getBrand().getBrandId()).getBrandName() + " " + carDraft.getModel() + " " + carDraft.getProductionYear();
            carDraft.setCarName(carName);
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

    @Override
    public Car convertCarDraftToCar(CarDraft carDraft) {
        try{
            Car car = new Car();
            car.setLicensePlate(carDraft.getLicensePlate());
            car.setModel(carDraft.getModel());
            car.setColor(carDraft.getColor());
            car.setSeat(carDraft.getSeat());
            car.setProductionYear(carDraft.getProductionYear());
            car.setTransmission(carDraft.getTransmission());
            car.setFuelType(carDraft.getFuelType());
            car.setMileage(carDraft.getMileage());
            car.setFuelConsumption(carDraft.getFuelConsumption());
            car.setBasePrice(carDraft.getBasePrice());
            car.setDeposit(carDraft.getDeposit());
            car.setDescription(carDraft.getDescription());
            car.setTerms(carDraft.getTerms());
            car.setCarPrice(carDraft.getCarPrice());
            car.setBrand(carDraft.getBrand());
            car.setLastModified(new Date());
            car.setDescription(carDraft.getDescription());
//            setCarStatus(car);
            //Set car additional Functions
            setCarAdditionalFunction(carDraft, car);
            //Set Address for car
            setCarAddress(carDraft, car);
            //Set car files
            car.setFrontImage(carDraft.getFrontImage());
            car.setBackImage(carDraft.getBackImage());
            car.setLeftImage(carDraft.getLeftImage());
            car.setRightImage(carDraft.getRightImage());
            car.setRegistration(carDraft.getRegistration());
            car.setCertificate(carDraft.getCertificate());
            car.setInsurance(carDraft.getInsurance());
            return car;
        }catch (Exception e){
            System.out.println("Something wrong when convert car draft to car in car owner service" + e.getMessage());
            return null;
        }
    }

    @Override
    public CarDraft convertCarToCarDraft(Car car) {
        try{
            CarDraft carDraft = new CarDraft();
            carDraft.setLicensePlate(car.getLicensePlate());
            carDraft.setModel(car.getModel());
            carDraft.setColor(car.getColor());
            carDraft.setSeat(car.getSeat());
            carDraft.setProductionYear(car.getProductionYear());
            carDraft.setTransmission(car.getTransmission());
            carDraft.setFuelType(car.getFuelType());
            carDraft.setMileage(car.getMileage());
            carDraft.setFuelConsumption(car.getFuelConsumption());
            carDraft.setBasePrice(car.getBasePrice());
            carDraft.setDeposit(car.getDeposit());
            carDraft.setDescription(car.getDescription());
            carDraft.setTerms(car.getTerms());
            carDraft.setCarPrice(car.getCarPrice());
            carDraft.setBrand(car.getBrand());
            carDraft.setDescription(car.getDescription());
            //Set carDraft additional Functions
            StringBuilder additionalFunction = new StringBuilder();
            for (AdditionalFunction function : car.getAdditionalFunctions()) {
                additionalFunction.append(function.getFunctionId()).append(",");
            }
            carDraft.setAdditionalFunction(additionalFunction.toString());
            //Set Address for carDraft
            carDraft.setProvince(car.getAddress().getProvinceId() + "," + car.getAddress().getProvince());
            carDraft.setDistrict(car.getAddress().getDistrictId() + "," + car.getAddress().getDistrict());
            carDraft.setWard(car.getAddress().getWardId() + "," + car.getAddress().getWard());
            carDraft.setHome(car.getAddress().getStreet());
            //Set car files
            carDraft.setFrontImage(car.getFrontImage());
            carDraft.setBackImage(car.getBackImage());
            carDraft.setLeftImage(car.getLeftImage());
            carDraft.setRightImage(car.getRightImage());
            carDraft.setRegistration(car.getRegistration());
            carDraft.setCertificate(car.getCertificate());
            carDraft.setInsurance(car.getInsurance());
            carDraft.setUser(car.getUser());
            return carDraft;
        }catch (Exception e){
            System.out.println("Something wrong when convert car to car draft in car owner service" + e.getMessage());
            return null;
        }
    }


    private void setCarStatus(Car car){
        CarStatus carStatus = new CarStatus();
        carStatus.setStatusId(1);
        car.setCarStatus(carStatus);
    }

    private void setCarAddress(CarDraft carDraft, Car car){
        int provinceId = 0;
        int districtId = 0;
        int wardId = 0;
        String province = "";
        String district = "";
        String ward = "";
        String home = carDraft.getHome();
        try{
            provinceId = Integer.parseInt(carDraft.getProvince().split(",")[0]);
            districtId = Integer.parseInt(carDraft.getDistrict().split(",")[0]);
            wardId = Integer.parseInt(carDraft.getWard().split(",")[0]);
            province = carDraft.getProvince().split(",")[1];
            district = carDraft.getDistrict().split(",")[1];
            ward = carDraft.getWard().split(",")[1];
        }catch (Exception e){
            System.out.println("Something wrong when parse Integer with the address Ids");
        }
        CarAddress address = new CarAddress();
        address.setProvinceId(provinceId);
        address.setDistrictId(districtId);
        address.setWardId(wardId);
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);
        address.setStreet(home);
        car.setAddress(address);
    }

    private void setCarAdditionalFunction(CarDraft carDraft, Car car) {
        try {
            Set<AdditionalFunction> additionalFunctions = new HashSet<>();
            String[] functionIds = carDraft.getAdditionalFunction().split(",");
            for (String idStr : functionIds) {
                try {
                    Integer functionId = Integer.parseInt(idStr.trim());
                    additionalFunctionRepository.findById(functionId).ifPresent(additionalFunctions::add);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid function ID: " + idStr);
                }
            }
            car.setAdditionalFunctions(additionalFunctions);
        }catch (Exception e){
            System.out.println("Something wrong when set additional function for car in car owner service" + e.getMessage());
        }
    }




}
