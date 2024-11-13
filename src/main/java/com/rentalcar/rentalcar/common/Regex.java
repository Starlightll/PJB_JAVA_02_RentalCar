package com.rentalcar.rentalcar.common;

public class Regex {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String PHONE_REGEX = "^\\+?[0-9]\\d{1,14}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{6,}$";
    public static final String NAME_REGEX = "^[a-zA-Z ]{2,}$";
    public static final String LICENSE_PLATE_REGEX = "^[0-9]{2}[A-Z]{1}-[0-9]{5}$";
    public static final String MONEY_REGEX = "^\\d+(\\.\\d{1,2})?$";
    public static final String DISTANCE_REGEX = "^\\d+(\\.\\d{1,2})?$";
    public static final String MATERIAL_REGEX = "^\\d+(\\.\\d{1,2})?$";
    public static final String FUEL_CONSUMPTION_REGEX = "^\\d+(\\.\\d{1,2})?$";
    public static final String NATIONAL_ID_REGEX = "^\\d{9}|\\d{12}$";


}
