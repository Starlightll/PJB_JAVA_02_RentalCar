package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyProfileDto;
import jakarta.servlet.http.HttpSession;


public interface MyProfileService {
    void changePassword(MyProfileDto myProfileDto, HttpSession session);
}
