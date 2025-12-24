package com.yuanzhi.finledger.repository;

import com.yuanzhi.finledger.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByTransactionID(Integer transactionID);
    List<Payment> findByStatus(String status);
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' ORDER BY p.paymentDate ASC")
    List<Payment> findPendingPayments();
}


