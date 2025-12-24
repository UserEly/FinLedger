package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 账户科目实体类
 */
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 20, unique = true)
    private String code; // 科目编码
    
    @Column(nullable = false, length = 50)
    private String name; // 科目名称
    
    @Column(nullable = false, length = 20)
    private String category; // 科目类别：ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    
    @Column(name = "debit_amount", precision = 10, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO; // 借方金额
    
    @Column(name = "credit_amount", precision = 10, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO; // 贷方金额
    
    @Column(length = 10)
    private String currency = "CNY"; // 货币类型
    
    @Column(name = "parent_id")
    private Integer parentID; // 父科目ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Account parent;
}


