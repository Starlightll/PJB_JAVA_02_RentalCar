package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import jakarta.servlet.http.HttpSession;

import java.util.List;


public interface IUserService {
    void saveUser(UserInfoDto user, HttpSession session);
    boolean checkPhone(String phone);
    List<UserDto> getAllUsers();
}
