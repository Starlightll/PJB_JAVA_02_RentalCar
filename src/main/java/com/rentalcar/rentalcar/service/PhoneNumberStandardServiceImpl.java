package com.rentalcar.rentalcar.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rentalcar.rentalcar.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberStandardServiceImpl implements PhoneNumberStandardService {
    @Autowired
    UserRepo userRepo;

    @Override
    public String normalizePhoneNumber(String phoneNumber, String regionCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, regionCode);
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber, String regionCode) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber, regionCode);
        return userRepo.existsByPhone(normalizedPhone);
    }

}
