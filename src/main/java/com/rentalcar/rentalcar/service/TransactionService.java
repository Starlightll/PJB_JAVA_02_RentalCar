package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.entity.TransactionType;
import com.rentalcar.rentalcar.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactionsByDateRange(Long userId, LocalDate fromDate, LocalDate toDate);

    void saveTransaction(User user, BigDecimal amount, String type, Booking booking);
}
