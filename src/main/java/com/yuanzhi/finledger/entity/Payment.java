package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体类
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "transaction_id", nullable = false)
    private Integer transactionID; // 关联交易ID
    
    @Column(name = "account_id", nullable = false)
    private Integer accountID; // 关联科目ID
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 支付金额
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // 支付日期
    
    @Column(length = 20)
    private String status = "PENDING"; // 状态：PENDING, APPROVED, REJECTED, PAID
    
    @Column(name = "approved_by")
    private Integer approvedBy; // 审批人ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    private Transaction transaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", insertable = false, updatable = false)
    private User approver;
}


