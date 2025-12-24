package com.yuanzhi.finledger.controller;

import com.yuanzhi.finledger.entity.Account;
import com.yuanzhi.finledger.exception.ForbiddenException;
import com.yuanzhi.finledger.service.AccountService;
import com.yuanzhi.finledger.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 账户控制器
 * 处理会计科目相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * 创建账户科目
     * 仅财务经理可以创建
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        log.info("创建账户科目请求: code={}", account.getCode());
        if (!SecurityUtil.isManager()) {
            throw new ForbiddenException("只有财务经理可以创建账户科目");
        }
        return ResponseEntity.ok(accountService.createAccount(account));
    }
    
    /**
     * 获取所有账户
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        log.debug("获取所有账户");
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
    
    /**
     * 根据类别获取账户
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Account>> getAccountsByCategory(@PathVariable String category) {
        log.debug("根据类别获取账户: category={}", category);
        return ResponseEntity.ok(accountService.getAccountsByCategory(category));
    }
    
    /**
     * 获取根账户
     */
    @GetMapping("/root")
    public ResponseEntity<List<Account>> getRootAccounts() {
        log.debug("获取根账户");
        return ResponseEntity.ok(accountService.getRootAccounts());
    }
    
    /**
     * 获取子账户
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Account>> getChildAccounts(@PathVariable Integer parentId) {
        log.debug("获取子账户: parentId={}", parentId);
        return ResponseEntity.ok(accountService.getChildAccounts(parentId));
    }
    
    /**
     * 根据ID获取账户
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        log.debug("查询账户: accountId={}", id);
        return ResponseEntity.ok(accountService.getAccountById(id));
    }
    
    /**
     * 更新账户信息
     * 仅财务经理可以更新
     */
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Integer id, @RequestBody Account account) {
        log.info("更新账户信息: accountId={}", id);
        if (!SecurityUtil.isManager()) {
            throw new ForbiddenException("只有财务经理可以更新账户信息");
        }
        return ResponseEntity.ok(accountService.updateAccount(id, account));
    }
    
    /**
     * 删除账户
     * 仅财务经理可以删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        log.info("删除账户: accountId={}", id);
        if (!SecurityUtil.isManager()) {
            throw new ForbiddenException("只有财务经理可以删除账户");
        }
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }
}

