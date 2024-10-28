package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.UserInfoDto;
import jakarta.servlet.http.HttpSession;


public interface IUserService {
    void saveUser(UserInfoDto user, HttpSession session);

}
