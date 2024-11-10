package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCarService implements SearchCarImp{
    @Autowired
    private CarRepository carRepository;
    @Override
    public List<Car> getCarList() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> findCars(String name, Pageable pageable) {
        return carRepository.findCarByCarName(name,name, pageable);
    }



    @Override
    public Page<Car> findCars(String name, Integer pageNo , Sort sort) {
        Pageable pageable = PageRequest.of(pageNo-1,5, sort);

        List<Car> list = this.findCars(name , pageable);
        int start = (int) pageable.getOffset();
        int end = (pageable.getOffset() + pageable.getPageSize()) >list.size() ? list.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        list = list.subList(start, end);
        return new PageImpl<Car>(list,pageable,this.findCars(name,pageable).size());
    }

    @Override
    public Page<Car> getAllCars(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1,5);
        return this.carRepository.findAll(pageable);
    }


}