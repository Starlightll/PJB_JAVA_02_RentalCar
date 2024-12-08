package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.UserDto;
import com.rentalcar.rentalcar.dto.UserInfoDto;
import com.rentalcar.rentalcar.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IUserService {
    void saveUser(UserInfoDto user, HttpSession session);
    boolean checkPhone(String phone);
    List<UserDto> getAllUsers();
    User addUser(User user, Integer roleId);
    void setUserRole(User user, Integer roleId);
    void setUserStatus(User user, UserStatus statusId);
    void setUserDefaultAvatar(User user);
    void updateUser(User user, MultipartFile drivingLicenseFile);
    void updateProfile(User user, MultipartFile drivingLicenseFile);
    void setUserAvatar(User user, MultipartFile avatarFile);
    boolean checkNationalId(String nationalId);
}
