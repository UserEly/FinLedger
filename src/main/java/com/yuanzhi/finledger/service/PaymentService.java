package com.yuanzhi.finledger.service;

import com.yuanzhi.finledger.entity.Payment;
import com.yuanzhi.finledger.exception.BusinessException;
import com.yuanzhi.finledger.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付服务类
 * 处理支付相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    /**
     * 创建支付记录
     */
    @Transactional
    public Payment createPayment(Payment payment) {
        log.info("创建支付记录: transactionId={}, amount={}", 
                payment.getTransactionID(), payment.getAmount());
        
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("支付记录创建成功: paymentId={}", savedPayment.getId());
        return savedPayment;
    }
    
    /**
     * 根据ID获取支付记录
     */
    public Payment getPaymentById(Integer id) {
        log.debug("查询支付记录: paymentId={}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("支付记录不存在: paymentId={}", id);
                    return new BusinessException("支付记录不存在");
                });
    }
    
    /**
     * 获取所有支付记录
     */
    public List<Payment> getAllPayments() {
        log.debug("查询所有支付记录");
        return paymentRepository.findAll();
    }
    
    /**
     * 获取待审批支付
     */
    public List<Payment> getPendingPayments() {
        log.debug("查询待审批支付");
        return paymentRepository.findPendingPayments();
    }
    
    /**
     * 根据交易ID获取支付记录
     */
    public List<Payment> getPaymentsByTransaction(Integer transactionId) {
        log.debug("根据交易ID查询支付记录: transactionId={}", transactionId);
        return paymentRepository.findByTransactionID(transactionId);
    }
    
    /**
     * 审批支付
     */
    @Transactional
    public Payment approvePayment(Integer id, Integer approverId) {
        log.info("审批支付: paymentId={}, approverId={}", id, approverId);
        Payment payment = getPaymentById(id);
        payment.setStatus("APPROVED");
        payment.setApprovedBy(approverId);
        Payment approvedPayment = paymentRepository.save(payment);
        log.info("支付审批成功: paymentId={}", id);
        return approvedPayment;
    }
    
    /**
     * 拒绝支付
     */
    @Transactional
    public Payment rejectPayment(Integer id, Integer approverId) {
        log.info("拒绝支付: paymentId={}, approverId={}", id, approverId);
        Payment payment = getPaymentById(id);
        payment.setStatus("REJECTED");
        payment.setApprovedBy(approverId);
        Payment rejectedPayment = paymentRepository.save(payment);
        log.info("支付已拒绝: paymentId={}", id);
        return rejectedPayment;
    }
    
    /**
     * 完成支付
     */
    @Transactional
    public Payment completePayment(Integer id) {
        log.info("完成支付: paymentId={}", id);
        Payment payment = getPaymentById(id);
        payment.setStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());
        Payment completedPayment = paymentRepository.save(payment);
        log.info("支付完成: paymentId={}", id);
        return completedPayment;
    }
    
    /**
     * 删除支付记录
     */
    @Transactional
    public void deletePayment(Integer id) {
        log.info("删除支付记录: paymentId={}", id);
        getPaymentById(id); // 验证存在
        paymentRepository.deleteById(id);
        log.info("支付记录删除成功: paymentId={}", id);
    }
}

