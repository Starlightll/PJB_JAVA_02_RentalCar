package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.TransactionType;

import java.math.BigDecimal;

public interface MyWalletService {

    void topUp(Long userId, BigDecimal amount);

    void withdraw(Long userId, BigDecimal amount);

    void transfer(Long senderId, Long receiverId, TransactionType senderType, TransactionType receiverType, BigDecimal amount, String description);

}
