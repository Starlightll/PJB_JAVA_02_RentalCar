package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.request.UpdateUserInfoRequest;
import com.rentalcar.rentalcar.request.UserInfoRequest;
import com.rentalcar.rentalcar.response.UserInfoResponse;

public interface IUserService {
    UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest);
     User saveUser(User user);
    boolean updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest);
}
