package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.entity.TransactionType;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.TransactionRepository;
import com.rentalcar.rentalcar.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class MyWalletServiceImpl implements MyWalletService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private TransactionService transactionService;

    @Override
    public void topUp(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setWallet(user.getWallet().add(amount));
        userRepository.save(user);

        transactionService.saveTransaction(user, amount, TransactionType.TOP_UP, null);
    }

    @Override
    public void withdraw(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getWallet().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setWallet(user.getWallet().subtract(amount));
        userRepository.save(user);

        transactionService.saveTransaction(user, amount, TransactionType.WITHDRAW, null);
    }

    @Override
    @Transactional
    public void transfer(Long senderId, Long receiverId, String senderType, String receiverType, BigDecimal amount, String description) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if(sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        if(sender.getStatus().equals(UserStatus.LOCKED) || receiver.getStatus().equals(UserStatus.LOCKED)) {
            throw new IllegalArgumentException("User is locked");
        }

        if (sender.getWallet().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        sender.setWallet(sender.getWallet().subtract(amount));
        receiver.setWallet(receiver.getWallet().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        transactionService.saveTransactionHistory(sender, receiver, senderType, receiverType, amount, description);
    }


}
