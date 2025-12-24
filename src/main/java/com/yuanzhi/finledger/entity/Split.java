package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分录拆分实体类
 */
@Entity
@Table(name = "splits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Split {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "entry_id", nullable = false)
    private Integer entryID; // 关联分录ID
    
    @Column(name = "account_id", nullable = false)
    private Integer accountID; // 关联科目ID
    
    private Integer quantity; // 数量
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // 单价
    
    @Column(name = "debit_amount", precision = 10, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO; // 借方金额
    
    @Column(name = "credit_amount", precision = 10, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO; // 贷方金额
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO; // 税额
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", insertable = false, updatable = false)
    private Entry entry;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
}


