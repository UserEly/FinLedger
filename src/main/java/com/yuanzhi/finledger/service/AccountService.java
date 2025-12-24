package com.yuanzhi.finledger.service;

import com.yuanzhi.finledger.entity.Account;
import com.yuanzhi.finledger.exception.BusinessException;
import com.yuanzhi.finledger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账户服务类
 * 处理会计科目相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    /**
     * 创建账户科目
     */
    @Transactional
    public Account createAccount(Account account) {
        log.info("创建账户科目: code={}, name={}", account.getCode(), account.getName());
        
        if (accountRepository.existsByCode(account.getCode())) {
            log.warn("创建账户失败: 科目编码已存在 - {}", account.getCode());
            throw new BusinessException("科目编码已存在");
        }
        
        Account savedAccount = accountRepository.save(account);
        log.info("账户科目创建成功: accountId={}, code={}", savedAccount.getId(), savedAccount.getCode());
        return savedAccount;
    }
    
    /**
     * 根据ID获取账户
     */
    public Account getAccountById(Integer id) {
        log.debug("查询账户: accountId={}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("账户不存在: accountId={}", id);
                    return new BusinessException("科目不存在");
                });
    }
    
    /**
     * 获取所有账户
     */
    public List<Account> getAllAccounts() {
        log.debug("查询所有账户");
        return accountRepository.findAll();
    }
    
    /**
     * 根据类别获取账户
     */
    public List<Account> getAccountsByCategory(String category) {
        log.debug("根据类别查询账户: category={}", category);
        return accountRepository.findByCategory(category);
    }
    
    /**
     * 获取根账户
     */
    public List<Account> getRootAccounts() {
        log.debug("查询根账户");
        return accountRepository.findByParentIDIsNull();
    }
    
    /**
     * 获取子账户
     */
    public List<Account> getChildAccounts(Integer parentId) {
        log.debug("查询子账户: parentId={}", parentId);
        return accountRepository.findByParentID(parentId);
    }
    
    /**
     * 更新账户信息
     */
    @Transactional
    public Account updateAccount(Integer id, Account accountDetails) {
        log.info("更新账户信息: accountId={}", id);
        Account account = getAccountById(id);
        if (accountDetails.getName() != null) {
            account.setName(accountDetails.getName());
        }
        if (accountDetails.getCategory() != null) {
            account.setCategory(accountDetails.getCategory());
        }
        if (accountDetails.getCurrency() != null) {
            account.setCurrency(accountDetails.getCurrency());
        }
        if (accountDetails.getParentID() != null) {
            account.setParentID(accountDetails.getParentID());
        }
        Account updatedAccount = accountRepository.save(account);
        log.info("账户信息更新成功: accountId={}", id);
        return updatedAccount;
    }
    
    /**
     * 删除账户
     */
    @Transactional
    public void deleteAccount(Integer id) {
        log.info("删除账户: accountId={}", id);
        // 检查科目是否存在
        getAccountById(id);
        // 检查是否有子科目
        List<Account> children = accountRepository.findByParentID(id);
        if (!children.isEmpty()) {
            log.warn("删除账户失败: 存在子科目 - accountId={}", id);
            throw new BusinessException("该科目下存在子科目，无法删除");
        }
        accountRepository.deleteById(id);
        log.info("账户删除成功: accountId={}", id);
    }
}

