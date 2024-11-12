package com.rentalcar.rentalcar.repository;

import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate fromDate, LocalDate toDate);

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate fromDate, LocalDate toDate, Pageable pageable);

}
