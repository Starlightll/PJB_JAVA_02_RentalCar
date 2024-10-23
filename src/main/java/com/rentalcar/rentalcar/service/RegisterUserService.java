package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.RegisterDto;

public interface RegisterUserService {
    public void registerUser(RegisterDto userDto);

    public String confirmToken(String token);

    public void resendActivationToken(String email);


}
