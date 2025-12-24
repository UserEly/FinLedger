package com.yuanzhi.finledger.service;

import com.yuanzhi.finledger.entity.Entry;
import com.yuanzhi.finledger.entity.Split;
import com.yuanzhi.finledger.entity.Transaction;
import com.yuanzhi.finledger.exception.BusinessException;
import com.yuanzhi.finledger.repository.EntryRepository;
import com.yuanzhi.finledger.repository.SplitRepository;
import com.yuanzhi.finledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分录服务类
 * 处理分录相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntryService {
    
    private final EntryRepository entryRepository;
    private final SplitRepository splitRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * 创建分录
     * 验证借贷平衡后保存分录和拆分
     */
    @Transactional
    public Entry createEntry(Entry entry, List<Split> splits) {
        log.info("创建分录: transactionId={}, summary={}", entry.getTransactionID(), entry.getSummary());
        
        // 验证借贷平衡
        BigDecimal totalDebit = splits.stream()
                .map(Split::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCredit = splits.stream()
                .map(Split::getCreditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalDebit.compareTo(totalCredit) != 0) {
            log.error("创建分录失败: 借贷金额不平衡 - 借方总额={}, 贷方总额={}", totalDebit, totalCredit);
            throw new BusinessException("借贷金额不平衡，借方总额：" + totalDebit + "，贷方总额：" + totalCredit);
        }
        
        // 保存分录
        Entry savedEntry = entryRepository.save(entry);
        log.info("分录创建成功: entryId={}", savedEntry.getId());
        
        // 保存分录拆分
        for (Split split : splits) {
            split.setEntryID(savedEntry.getId());
            splitRepository.save(split);
        }
        log.debug("分录拆分保存成功: splitCount={}", splits.size());
        
        // 更新交易状态
        Transaction transaction = transactionRepository.findById(entry.getTransactionID())
                .orElseThrow(() -> {
                    log.error("交易记录不存在: transactionId={}", entry.getTransactionID());
                    return new BusinessException("交易记录不存在");
                });
        transaction.setStatus("POSTED");
        transactionRepository.save(transaction);
        log.info("交易状态已更新为已过账: transactionId={}", entry.getTransactionID());
        
        return savedEntry;
    }
    
    /**
     * 根据ID获取分录
     */
    public Entry getEntryById(Integer id) {
        log.debug("查询分录: entryId={}", id);
        return entryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("分录不存在: entryId={}", id);
                    return new BusinessException("分录不存在");
                });
    }
    
    /**
     * 获取所有分录
     */
    public List<Entry> getAllEntries() {
        log.debug("查询所有分录");
        return entryRepository.findAll();
    }
    
    /**
     * 获取待审核分录
     */
    public List<Entry> getSubmittedEntries() {
        log.debug("查询待审核分录");
        return entryRepository.findSubmittedEntries();
    }
    
    /**
     * 根据交易ID获取分录
     */
    public List<Entry> getEntriesByTransaction(Integer transactionId) {
        log.debug("根据交易ID查询分录: transactionId={}", transactionId);
        return entryRepository.findByTransactionID(transactionId);
    }
    
    /**
     * 根据分录ID获取拆分
     */
    public List<Split> getSplitsByEntry(Integer entryId) {
        log.debug("查询分录拆分: entryId={}", entryId);
        return splitRepository.findByEntryID(entryId);
    }
    
    /**
     * 更新分录状态
     */
    @Transactional
    public Entry updateEntryStatus(Integer id, String status) {
        log.info("更新分录状态: entryId={}, status={}", id, status);
        Entry entry = getEntryById(id);
        entry.setStatus(status);
        Entry updatedEntry = entryRepository.save(entry);
        log.info("分录状态更新成功: entryId={}, status={}", id, status);
        return updatedEntry;
    }
    
    /**
     * 删除分录
     */
    @Transactional
    public void deleteEntry(Integer id) {
        log.info("删除分录: entryId={}", id);
        // 删除关联的分录拆分
        List<Split> splits = splitRepository.findByEntryID(id);
        splitRepository.deleteAll(splits);
        log.debug("已删除分录拆分: splitCount={}", splits.size());
        
        // 删除分录
        entryRepository.deleteById(id);
        log.info("分录删除成功: entryId={}", id);
    }
}

