package com.yuanzhi.finledger.service;

import com.yuanzhi.finledger.entity.Transaction;
import com.yuanzhi.finledger.exception.BusinessException;
import com.yuanzhi.finledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易服务类
 * 处理交易记录相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    
    /**
     * 创建交易记录
     */
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        log.info("创建交易记录: supplierClient={}, totalAmount={}", 
                transaction.getSupplierClient(), transaction.getTotalAmount());
        
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("交易记录创建成功: transactionId={}", savedTransaction.getId());
        return savedTransaction;
    }
    
    /**
     * 根据ID获取交易记录
     */
    public Transaction getTransactionById(Integer id) {
        log.debug("查询交易记录: transactionId={}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("交易记录不存在: transactionId={}", id);
                    return new BusinessException("交易记录不存在");
                });
    }
    
    /**
     * 获取所有交易记录
     */
    public List<Transaction> getAllTransactions() {
        log.debug("查询所有交易记录");
        return transactionRepository.findAll();
    }
    
    /**
     * 获取待过账交易
     */
    public List<Transaction> getPendingTransactions() {
        log.debug("查询待过账交易");
        return transactionRepository.findPendingTransactions();
    }
    
    /**
     * 根据用户ID获取交易记录
     */
    public List<Transaction> getTransactionsByUser(Integer userId) {
        log.debug("根据用户ID查询交易记录: userId={}", userId);
        return transactionRepository.findByUserID(userId);
    }
    
    /**
     * 更新交易记录
     */
    @Transactional
    public Transaction updateTransaction(Integer id, Transaction transactionDetails) {
        log.info("更新交易记录: transactionId={}", id);
        Transaction transaction = getTransactionById(id);
        if (transactionDetails.getSupplierClient() != null) {
            transaction.setSupplierClient(transactionDetails.getSupplierClient());
        }
        if (transactionDetails.getProject() != null) {
            transaction.setProject(transactionDetails.getProject());
        }
        if (transactionDetails.getDueDate() != null) {
            transaction.setDueDate(transactionDetails.getDueDate());
        }
        if (transactionDetails.getProductService() != null) {
            transaction.setProductService(transactionDetails.getProductService());
        }
        if (transactionDetails.getQuantity() != null) {
            transaction.setQuantity(transactionDetails.getQuantity());
        }
        if (transactionDetails.getUnitPrice() != null) {
            transaction.setUnitPrice(transactionDetails.getUnitPrice());
        }
        if (transactionDetails.getTotalAmount() != null) {
            transaction.setTotalAmount(transactionDetails.getTotalAmount());
        }
        if (transactionDetails.getTaxRate() != null) {
            transaction.setTaxRate(transactionDetails.getTaxRate());
        }
        if (transactionDetails.getStatus() != null) {
            transaction.setStatus(transactionDetails.getStatus());
        }
        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("交易记录更新成功: transactionId={}", id);
        return updatedTransaction;
    }
    
    /**
     * 删除交易记录
     */
    @Transactional
    public void deleteTransaction(Integer id) {
        log.info("删除交易记录: transactionId={}", id);
        getTransactionById(id); // 验证存在
        transactionRepository.deleteById(id);
        log.info("交易记录删除成功: transactionId={}", id);
    }
}

