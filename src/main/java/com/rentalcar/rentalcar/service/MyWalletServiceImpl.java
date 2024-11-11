package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MyWalletServiceImpl implements MyWalletService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public void topUp(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setWallet(user.getWallet().add(amount));
        userRepository.save(user);
    }

    @Override
    public void withdraw(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getWallet().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setWallet(user.getWallet().subtract(amount));
        userRepository.save(user);
    }
}