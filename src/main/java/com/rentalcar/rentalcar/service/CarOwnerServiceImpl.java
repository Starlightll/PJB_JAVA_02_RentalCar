package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.repository.AdditionalFunctionRepository;
import com.rentalcar.rentalcar.repository.CarDraftRepository;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.apache.poi.ss.formula.functions.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.aspectj.util.FileUtil.copyFiles;

@Service
public class CarOwnerServiceImpl implements CarOwnerService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    AdditionalFunctionRepository additionalFunctionRepository;
    @Autowired
    private CarDraftRepository carDraftRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private CarDraftService carDraftService;

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
    public void updateCar(Car car) {

    }

    @Override
    public void deleteCar(int carId) {

    }

    @Override
    @Transactional
    public void saveCar(CarDraft carDraft, User user) {
        try{
            Car car = new Car();
            car.setUser(user);
//            if(carRepository.findFirstLicensePlateMatchNative(carDraft.getLicensePlate()) != null){
//                System.out.println("License plate already exists");
//                throw new Exception("License plate already exists");
//            }else{
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

    private void updateCar(){

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


    private void setCarFiles(Car car, CarDraft carDraft, User user){
        //Car files
        Path sourceFolder = Paths.get("uploads/CarOwner/"+user.getId()+"/Draft/", carDraft.getDraftId()+"");
        Path targetFolder = Paths.get("uploads/CarOwner/"+user.getId()+"/Car/", car.getCarId()+"");
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

