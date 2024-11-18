package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.RegisterDto;

public interface RegisterUserService {
    void registerUser(RegisterDto userDto);

    String confirmToken(String token);

    void resendActivationToken(String email);


}
