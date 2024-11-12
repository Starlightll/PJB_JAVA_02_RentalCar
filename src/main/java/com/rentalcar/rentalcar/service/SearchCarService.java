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
    public List<Car> findCarList(String name, Sort sort) {
        return carRepository.findCarByCarName(name,name, sort);
    }



    @Override
    public Page<Car> findCars(String name, Integer pageNo, Sort sort) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5, sort);

        // Fetch the entire list of cars that match the name
        List<Car> fullList = findCarList(name, sort);  // Assuming this fetches all matches without pagination

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), fullList.size());
        List<Car> paginatedList = fullList.subList(start, end);

        // Return a PageImpl with the paginated list, the pageable, and the total count
        return new PageImpl<>(paginatedList, pageable, fullList.size());
    }


    @Override
    public Page<Car> getAllCars(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo-1,5);
        return this.carRepository.findAll(pageable);
    }


}