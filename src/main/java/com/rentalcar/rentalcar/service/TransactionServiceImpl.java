package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.TransactionRepository;
import com.rentalcar.rentalcar.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private HttpSession httpSession;

    @Override
    public List<Transaction> getTransactionsByDateRange(Long userId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);

        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, fromDateTime, toDateTime);
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
        //Update user's wallet to session after a transaction
        if(user.getId() == ((User) httpSession.getAttribute("user")).getId()) {
            User updateUser =  userRepo.getUserById(user.getId());
            BigDecimal updateWallet = updateUser.getWallet();
            user.setWallet(updateWallet);
            httpSession.setAttribute("user", user);
        }
    }
}
