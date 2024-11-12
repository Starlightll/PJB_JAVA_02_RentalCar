package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.entity.TransactionType;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactionsByDateRange(Long userId, LocalDate fromDate, LocalDate toDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, fromDate, toDate);
    }


    @Override
    public void saveTransaction(User user, BigDecimal amount, String type, Booking booking) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(type);
        if (booking != null) {
            transaction.setBooking(booking);
        }
        transactionRepository.save(transaction);
    }
}
