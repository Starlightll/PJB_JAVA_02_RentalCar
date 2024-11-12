package com.rentalcar.rentalcar.service;

import java.math.BigDecimal;

public interface MyWalletService {

    void topUp(Long userId, BigDecimal amount);

    void withdraw(Long userId, BigDecimal amount);

}
