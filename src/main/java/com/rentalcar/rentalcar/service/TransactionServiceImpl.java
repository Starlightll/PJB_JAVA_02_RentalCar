package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.repository.TransactionRepository;
import com.rentalcar.rentalcar.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void saveTransaction(Transaction transaction) {
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}
