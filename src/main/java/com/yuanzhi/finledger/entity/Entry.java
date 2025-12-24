package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分录实体类
 */
@Entity
@Table(name = "entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 255)
    private String summary; // 摘要
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 总金额
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate; // 创建日期
    
    @Column(length = 20)
    private String status = "DRAFT"; // 状态：DRAFT, SUBMITTED, APPROVED, REJECTED
    
    @Column(name = "transaction_id", nullable = false)
    private Integer transactionID; // 关联交易ID
    
    @Column(name = "user_id", nullable = false)
    private Integer userID; // 录入用户ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    private Transaction transaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}


