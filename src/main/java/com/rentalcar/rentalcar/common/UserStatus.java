package com.rentalcar.rentalcar.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    PENDING("PENDING"),
    ACTIVATED("ACTIVATED"),
    LOCKED("LOCKED"),
    DELETED("DELETED"),
    RENTED("RENTED");

    private final String status;
}
