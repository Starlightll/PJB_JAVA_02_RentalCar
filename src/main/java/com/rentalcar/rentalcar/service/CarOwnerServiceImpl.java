package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.BrandRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class CarOwnerServiceImpl implements CarOwnerService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private CarDraftRepository carDraftRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private CarDraftService carDraftService;
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Set<Car> listCars() {
        return null;
    }

    @Override
    public List<Car> findAllWithSortDesc(String field) {
        return carRepository.findAll(Sort.by(Sort.Direction.DESC, field));
    }

    @Override
    public List<Car> findAllWithSortAsc(String field) {
        return carRepository.findAll(Sort.by(Sort.Direction.ASC, field));
    }


    @Override
    public void updateCar(Car carUpdate, MultipartFile[] files, User user, Integer carId, Integer statusId) {
        try{
            Car car = carRepository.findById(carId).get();
            if(!Objects.equals(car.getUser().getId(), user.getId()) || car.getCarStatus().getStatusId() == 8 && user.getRoles().get(0).getId() != 1){
                throw new AuthenticationException("You are not allowed to update this car");
            }
            // Create folder path
            String folderName = String.format("%s", car.getCarId()+"");
            Path carFolderPath = Paths.get("uploads/User/"+user.getId()+"/Car/", folderName);
            Path imageFilePath;
            // Store each file if it exists
            //Front image
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                imageFilePath = Paths.get(car.getFrontImage());
                fileStorageService.deleteFile(imageFilePath);
                files[0].getSize();
                String storedPath = fileStorageService.storeFile(files[0], carFolderPath, "frontImage"+".png");
                car.setFrontImage(storedPath);
            }
            //Back image
            if (files[1] != null && !files[1].isEmpty() && files[1].getSize() > 0) {
                imageFilePath = Paths.get(car.getBackImage());
                fileStorageService.deleteFile(imageFilePath);
                files[1].getSize();
                String storedPath = fileStorageService.storeFile(files[1], carFolderPath, "backImage"+".png");
                car.setBackImage(storedPath);
            }
            //Left image
            if (files[2] != null && !files[2].isEmpty() && files[2].getSize() > 0) {
                imageFilePath = Paths.get(car.getLeftImage());
                fileStorageService.deleteFile(imageFilePath);
                files[2].getSize();
                String storedPath = fileStorageService.storeFile(files[2], carFolderPath, "leftImage"+".png");
                car.setLeftImage(storedPath);
            }
            //Right image
            if (files[3] != null && !files[3].isEmpty() && files[3].getSize() > 0) {
                imageFilePath = Paths.get(car.getRightImage());
                fileStorageService.deleteFile(imageFilePath);
                files[3].getSize();
                String storedPath = fileStorageService.storeFile(files[3], carFolderPath, "rightImage"+".png");
                car.setRightImage(storedPath);
            }
            //Details Information
            car.setMileage(carUpdate.getMileage());
            car.setFuelConsumption(carUpdate.getFuelConsumption());
            car.setDescription(carUpdate.getDescription());
            //Set car Address
            setCarAddress(car, carUpdate);
            //Set car Additional Functions
            setCarAdditionalFunction(car, carUpdate);
            car.setBasePrice(carUpdate.getBasePrice());
            car.setCarPrice(carUpdate.getCarPrice());
            car.setDeposit(carUpdate.getDeposit());
            car.setTerms(carUpdate.getTerms());
            car.setLastModified(new Date());
            CarStatus carStatus = new CarStatus();
            if(statusId != null){
                carStatus.setStatusId(statusId);
                car.setCarStatus(carStatus);
            }else{
                carStatus.setStatusId(1);
                car.setCarStatus(carStatus);
            }
            String carName =  brandRepository.findByBrandId(car.getBrand().getBrandId()).getBrandName()+ " " + car.getModel() + " " + car.getProductionYear();
            car.setCarName(carName);
            carRepository.save(car);
        }catch (Exception e){
            System.out.println("Something wrong when update car in car owner service" + e.getMessage());
            throw new RuntimeException("Something wrong when update car in car owner service");
        }
    }

    @Override
    public void deleteCar(int carId) {
        try{
            setCarStatus(carId, 4);
        }catch (Exception e){
            System.out.println("Something wrong when delete car in car owner service" + e.getMessage());
            throw new RuntimeException("Something wrong when delete car in car owner service");
        }
    }

    @Override
    @Transactional
    public void addCar(CarDraft carDraft, User user) {
        try{
            Car car = new Car();
            car.setUser(user);
               //Check if car license plate owned by other user
                Long userOwnLicence = carRepository.findFirstUserByLicensePlate(carDraft.getLicensePlate());
                if(userOwnLicence != null) {
                    if(!Objects.equals(userOwnLicence, user.getId())) {
                        throw new RuntimeException("Liscense plate already owned by another user");
                    }
                    car.setLicensePlate(carDraft.getLicensePlate());
                }else{
                    car.setLicensePlate(carDraft.getLicensePlate());
                }
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
                String carName =  brandRepository.findByBrandId(car.getBrand().getBrandId()).getBrandName()+ " " + car.getModel() + " " + car.getProductionYear();
                car.setCarName(carName);
                setCarStatus(car);
                //Set car additional Functions
                setCarAdditionalFunction(carDraft, car);
                carRepository.save(car);
                //Set Address for car
                setCarAddress(carDraft, car);
                car.getAddress().setCar(car);
                //Set car files
                setCarFiles(car, carDraft, user);
                carRepository.save(car);
                carDraftService.deleteDraftByUserId(user.getId());
//            }
        }catch (Exception e){
            System.out.println("Something wrong when save car from draft in car owner service" + e.getMessage());
            throw new RuntimeException("Something wrong when save car from draft in car owner service");
        }
    }

    @Override
    public List<Car> getCarsByStatus(Integer statusId) {
        return carRepository.findAllByCarStatus(statusId);
    }

    @Override
    public void setCarStatus(Integer carId, Integer statusId) {
        try{
            Car car = carRepository.getCarByCarId(carId);
            CarStatus carStatus = new CarStatus();
            carStatus.setStatusId(statusId);
            car.setCarStatus(carStatus);
            carRepository.save(car);
        }catch (Exception e){
            System.out.println("Something wrong when set car status in car owner service" + e.getMessage());
            throw new RuntimeException("Something wrong when set car status in car owner service");
        }
    }

    @Override
    public boolean requestChangeBasicInformation(CarDraft carDraft, MultipartFile[] files, User user, Integer carId) {
//        CarDraft carDraftPending = carDraftRepository.findTopByUser_IdAndCarIdAndVerifyStatusOrderByLastModifiedDesc(user.getId(), carId, "Pending");
        List<CarDraft> carDraftPendings = carDraftRepository.findCarDraftsByUser_IdAndCarIdAndVerifyStatus(user.getId(), carId, "Pending");
        //Set other pending request to cancelled
//        if(carDraftPending != null){
//            carDraftPending.setVerifyStatus("Cancelled");
//            carDraftRepository.save(carDraftPending);
//        }
        if(!carDraftPendings.isEmpty()){
            for(CarDraft carDraftPending : carDraftPendings){
                carDraftPending.setVerifyStatus("Cancelled");
                carDraftRepository.save(carDraftPending);
            }
        }
        //Set new request
        carDraft.setCarId(carId);
        carDraft.setVerifyStatus("Pending");
        carDraft.setLastModified(new Date());
        carDraftService.saveRequestChangeBasicInformation(carDraft, files, user);
        return true;
    }

    @Override
    public Set<String> getAllLicensePlates(Long userId) {
        Set<String> licensePlatesOfRequest = carDraftRepository.findAllLicensePlateByUser_IdAndVerifyStatus(userId, "Pending");
        Set<String> licensePlatesOfCar = carRepository.findAllLicensePlateByUserId(userId);
        licensePlatesOfRequest.addAll(licensePlatesOfCar);
        return licensePlatesOfRequest;
    }

    @Override
    public Set<String> getAllLicensePlatesNotOwnedByUser(Long userId) {
        Set<String> licensePlatesOfRequest = carDraftRepository.findAllLicensePlateNotOwnedByUser_IdAndVerifyStatus(userId, "Pending");
        Set<String> licensePlatesOfCar = carRepository.findAllLicensePlateNotOwnedByUserId(userId);
        licensePlatesOfRequest.addAll(licensePlatesOfCar);
        return licensePlatesOfRequest;
    }

    @Override
    public CarDto getRatingByCarId(Long carId) {
        Object[] outerArr = carRepository.getRatingByCarId(carId);
        Object[] avgRating = (Object[]) outerArr[0];
        Long car_id = avgRating[0] instanceof Integer ? Long.valueOf((Integer) avgRating[0]) : null;
        double averageRating = avgRating[1] != null ? (Double) avgRating[1] : 0;

        return new CarDto(car_id, averageRating);
    }

    private void setCarStatus(Car car){
        CarStatus carStatus = new CarStatus();
        carStatus.setStatusId(8);
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

    private void setCarAddress(Car car, Car carUpdate){
        try{
            CarAddress address = car.getAddress();
            address.setProvinceId(carUpdate.getAddress().getProvinceId());
            address.setDistrictId(carUpdate.getAddress().getDistrictId());
            address.setWardId(carUpdate.getAddress().getWardId());
            address.setProvince(carUpdate.getAddress().getProvince());
            address.setDistrict(carUpdate.getAddress().getDistrict());
            address.setWard(carUpdate.getAddress().getWard());
            address.setStreet(carUpdate.getAddress().getStreet());
            car.setAddress(address);
        }catch (Exception e){
            System.out.println("Something wrong when parse Integer with the address Ids");
        }
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

    private void setCarAdditionalFunction(Car car, Car carUpdate) {
        try {
            car.getAdditionalFunctions().clear();
            car.setAdditionalFunctions(carUpdate.getAdditionalFunctions());
        }catch (Exception e){
            System.out.println("Something wrong when set additional function for car in car owner service" + e.getMessage());
        }
    }


    private void setCarFiles(Car car, CarDraft carDraft, User user){
        //Car files
        Path sourceFolder = Paths.get("uploads/User/"+user.getId()+"/Draft/", carDraft.getDraftId()+"");
        Path targetFolder = Paths.get("uploads/User/"+user.getId()+"/Car/", car.getCarId()+"");
        if(fileStorageService.moveFiles(sourceFolder, targetFolder)){
            car.setFrontImage(carDraft.getFrontImage().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setBackImage(carDraft.getBackImage().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setLeftImage(carDraft.getLeftImage().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setRightImage(carDraft.getRightImage().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setRegistration(carDraft.getRegistration().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setCertificate(carDraft.getCertificate().replace(sourceFolder.toString(), targetFolder.toString()));
            car.setInsurance(carDraft.getInsurance().replace(sourceFolder.toString(), targetFolder.toString()));
        }else{
            System.out.println("Error when copy files from draft to car");
            throw new RuntimeException("Error when copy files from draft to car");
        }
    }

}

