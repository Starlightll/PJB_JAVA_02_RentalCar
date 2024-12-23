package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.TransactionType;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

public interface MyWalletService {

    void topUp(Long userId, BigDecimal amount);

    void withdraw(Long userId, BigDecimal amount);

    void transfer(Long senderId, Long receiverId, String senderType, String receiverType, BigDecimal amount, String description);

}
