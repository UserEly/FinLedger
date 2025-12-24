package com.yuanzhi.finledger.repository;

import com.yuanzhi.finledger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByStatus(String status);
    List<Transaction> findByUserID(Integer userID);
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' ORDER BY t.date ASC")
    List<Transaction> findPendingTransactions();
}


