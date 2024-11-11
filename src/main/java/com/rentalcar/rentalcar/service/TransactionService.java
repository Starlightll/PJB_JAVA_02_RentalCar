package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactionsByDateRange(Long userId, LocalDate fromDate, LocalDate toDate);

    void saveTransaction(Transaction transaction);
}
