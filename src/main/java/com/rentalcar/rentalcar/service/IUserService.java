package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import jakarta.servlet.http.HttpSession;

import java.util.List;


public interface IUserService {
    void saveUser(UserInfoDto user, HttpSession session);
    boolean checkPhone(String phone);
    List<UserDto> getAllUsers();
    User addUser(User user);
    void setUserRole(User user, Integer roleId);
    void setUserStatus(User user, UserStatus statusId);
}
