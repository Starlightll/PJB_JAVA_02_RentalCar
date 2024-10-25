package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.request.UpdateUserInfoRequest;
import com.rentalcar.rentalcar.request.UserInfoRequest;
import com.rentalcar.rentalcar.response.UserInfoResponse;

public interface IUserService {
    UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest);

    boolean updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest);
}
