package com.yuanzhi.finledger.controller;

import com.yuanzhi.finledger.entity.Transaction;
import com.yuanzhi.finledger.exception.ForbiddenException;
import com.yuanzhi.finledger.service.TransactionService;
import com.yuanzhi.finledger.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 交易控制器
 * 处理交易记录相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * 创建交易记录
     * 仅会计可以创建
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        log.info("创建交易记录请求");
        if (!SecurityUtil.isAccountant()) {
            throw new ForbiddenException("只有会计可以创建交易记录");
        }
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }
    
    /**
     * 获取所有交易记录
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        log.debug("获取所有交易记录");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    /**
     * 获取待过账交易
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactions() {
        log.debug("获取待过账交易");
        return ResponseEntity.ok(transactionService.getPendingTransactions());
    }
    
    /**
     * 根据用户ID获取交易记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(@PathVariable Integer userId) {
        log.debug("根据用户ID获取交易记录: userId={}", userId);
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }
    
    /**
     * 根据ID获取交易记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id) {
        log.debug("查询交易记录: transactionId={}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    /**
     * 更新交易记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody Transaction transaction) {
        log.info("更新交易记录: transactionId={}", id);
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }
    
    /**
     * 删除交易记录
     * 仅会计可以删除自己创建的交易
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        log.info("删除交易记录: transactionId={}", id);
        if (!SecurityUtil.isAccountant()) {
            throw new ForbiddenException("只有会计可以删除交易记录");
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}

