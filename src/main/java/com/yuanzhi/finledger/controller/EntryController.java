package com.yuanzhi.finledger.controller;

import com.yuanzhi.finledger.entity.Entry;
import com.yuanzhi.finledger.entity.Split;
import com.yuanzhi.finledger.exception.ForbiddenException;
import com.yuanzhi.finledger.service.EntryService;
import com.yuanzhi.finledger.util.SecurityUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 分录控制器
 * 处理分录相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EntryController {
    
    private final EntryService entryService;
    
    /**
     * 创建分录请求DTO
     */
    @Data
    static class CreateEntryRequest {
        @Valid
        private Entry entry;
        @Valid
        private List<Split> splits;
    }
    
    /**
     * 创建分录
     * 仅会计可以创建
     */
    @PostMapping
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody CreateEntryRequest request) {
        log.info("创建分录请求: transactionId={}", request.getEntry().getTransactionID());
        if (!SecurityUtil.isAccountant()) {
            throw new ForbiddenException("只有会计可以创建分录");
        }
        return ResponseEntity.ok(entryService.createEntry(request.getEntry(), request.getSplits()));
    }
    
    /**
     * 获取所有分录
     */
    @GetMapping
    public ResponseEntity<List<Entry>> getAllEntries() {
        log.debug("获取所有分录");
        return ResponseEntity.ok(entryService.getAllEntries());
    }
    
    /**
     * 获取待审核分录
     */
    @GetMapping("/submitted")
    public ResponseEntity<List<Entry>> getSubmittedEntries() {
        log.debug("获取待审核分录");
        return ResponseEntity.ok(entryService.getSubmittedEntries());
    }
    
    /**
     * 根据交易ID获取分录
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Entry>> getEntriesByTransaction(@PathVariable Integer transactionId) {
        log.debug("根据交易ID获取分录: transactionId={}", transactionId);
        return ResponseEntity.ok(entryService.getEntriesByTransaction(transactionId));
    }
    
    /**
     * 根据分录ID获取拆分
     */
    @GetMapping("/{id}/splits")
    public ResponseEntity<List<Split>> getSplitsByEntry(@PathVariable Integer id) {
        log.debug("查询分录拆分: entryId={}", id);
        return ResponseEntity.ok(entryService.getSplitsByEntry(id));
    }
    
    /**
     * 根据ID获取分录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entry> getEntryById(@PathVariable Integer id) {
        log.debug("查询分录: entryId={}", id);
        return ResponseEntity.ok(entryService.getEntryById(id));
    }
    
    /**
     * 更新分录状态
     * 会计可以提交，财务经理可以审核
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Entry> updateEntryStatus(@PathVariable Integer id, @RequestParam String status) {
        log.info("更新分录状态: entryId={}, status={}", id, status);
        
        // 会计可以提交（DRAFT -> SUBMITTED）
        if ("SUBMITTED".equals(status) && !SecurityUtil.isAccountant()) {
            throw new ForbiddenException("只有会计可以提交分录");
        }
        
        // 财务经理可以审核（SUBMITTED -> APPROVED/REJECTED）
        if (("APPROVED".equals(status) || "REJECTED".equals(status)) && !SecurityUtil.isManager()) {
            throw new ForbiddenException("只有财务经理可以审核分录");
        }
        
        return ResponseEntity.ok(entryService.updateEntryStatus(id, status));
    }
    
    /**
     * 删除分录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Integer id) {
        log.info("删除分录: entryId={}", id);
        entryService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}

