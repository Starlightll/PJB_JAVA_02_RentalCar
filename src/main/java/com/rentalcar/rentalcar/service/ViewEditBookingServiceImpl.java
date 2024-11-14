package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class ViewEditBookingServiceImpl implements ViewEditBookingService{
    @Override
    public MyBookingDto getBookingDetail(Integer bookingId, Integer carId, HttpSession session) {
        return null;
    }
}
