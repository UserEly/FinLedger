package com.yuanzhi.finledger.controller;

import com.yuanzhi.finledger.entity.Payment;
import com.yuanzhi.finledger.exception.ForbiddenException;
import com.yuanzhi.finledger.service.PaymentService;
import com.yuanzhi.finledger.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 支付控制器
 * 处理支付相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * 创建支付记录
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        log.info("创建支付记录请求: transactionId={}", payment.getTransactionID());
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
    
    /**
     * 获取所有支付记录
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.debug("获取所有支付记录");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    /**
     * 获取待审批支付
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        log.debug("获取待审批支付");
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }
    
    /**
     * 根据交易ID获取支付记录
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Payment>> getPaymentsByTransaction(@PathVariable Integer transactionId) {
        log.debug("根据交易ID获取支付记录: transactionId={}", transactionId);
        return ResponseEntity.ok(paymentService.getPaymentsByTransaction(transactionId));
    }
    
    /**
     * 根据ID获取支付记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        log.debug("查询支付记录: paymentId={}", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    
    /**
     * 审批支付
     * 财务经理和老板可以审批
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<Payment> approvePayment(@PathVariable Integer id, @RequestParam Integer approverId) {
        log.info("审批支付: paymentId={}, approverId={}", id, approverId);
        if (!SecurityUtil.isManagerOrBoss()) {
            throw new ForbiddenException("只有财务经理和老板可以审批支付");
        }
        return ResponseEntity.ok(paymentService.approvePayment(id, approverId));
    }
    
    /**
     * 拒绝支付
     * 财务经理和老板可以拒绝
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Payment> rejectPayment(@PathVariable Integer id, @RequestParam Integer approverId) {
        log.info("拒绝支付: paymentId={}, approverId={}", id, approverId);
        if (!SecurityUtil.isManagerOrBoss()) {
            throw new ForbiddenException("只有财务经理和老板可以拒绝支付");
        }
        return ResponseEntity.ok(paymentService.rejectPayment(id, approverId));
    }
    
    /**
     * 完成支付
     * 仅老板可以完成支付
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Integer id) {
        log.info("完成支付: paymentId={}", id);
        if (!SecurityUtil.isBoss()) {
            throw new ForbiddenException("只有老板可以完成支付");
        }
        return ResponseEntity.ok(paymentService.completePayment(id));
    }
    
    /**
     * 删除支付记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        log.info("删除支付记录: paymentId={}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }
}

