package com.yuanzhi.finledger.controller;

import com.yuanzhi.finledger.entity.User;
import com.yuanzhi.finledger.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 处理用户相关的API请求
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("获取所有用户列表");
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        log.debug("查询用户: userId={}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        log.info("更新用户信息: userId={}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }
}

