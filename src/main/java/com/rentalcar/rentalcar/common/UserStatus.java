package com.rentalcar.rentalcar.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    PENDING("PENDING"),
    ACTIVATED("ACTIVATED"),
    LOCKED("LOCKED"),
    DELETED("DELETED");

    private final String status;
}
