package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(name = "supplier_client", nullable = false, length = 100)
    private String supplierClient; // 供应商/客户
    
    @Column(length = 100)
    private String project; // 项目
    
    @Column(name = "due_date")
    private LocalDateTime dueDate; // 账期
    
    @Column(name = "product_service", length = 100)
    private String productService; // 产品/服务
    
    private Integer quantity; // 数量
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // 单价
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 总金额
    
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate; // 税率
    
    @Column(length = 20)
    private String status = "PENDING"; // 状态：PENDING, POSTED, PAID
    
    @Column(name = "user_id")
    private Integer userID; // 录入用户ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}


