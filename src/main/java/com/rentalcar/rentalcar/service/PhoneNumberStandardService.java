package com.rentalcar.rentalcar.service;


public interface PhoneNumberStandardService {
    String normalizePhoneNumber(String phoneNumber, String regionCode);

    boolean isPhoneNumberExists(String phoneNumber, String regionCode);
}
