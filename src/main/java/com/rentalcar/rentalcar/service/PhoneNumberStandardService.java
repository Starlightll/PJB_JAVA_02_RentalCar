package com.rentalcar.rentalcar.service;


public interface PhoneNumberStandardService {
    String normalizePhoneNumber(String phoneNumber, String regionCode, int countryCode);

    boolean isPhoneNumberExists(String phoneNumber, String regionCode, int countryCode);
}
