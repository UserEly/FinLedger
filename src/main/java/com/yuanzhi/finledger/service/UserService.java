package com.yuanzhi.finledger.service;

import com.yuanzhi.finledger.dto.AuthResponse;
import com.yuanzhi.finledger.dto.LoginRequest;
import com.yuanzhi.finledger.dto.RegisterRequest;
import com.yuanzhi.finledger.entity.User;
import com.yuanzhi.finledger.exception.BusinessException;
import com.yuanzhi.finledger.repository.UserRepository;
import com.yuanzhi.finledger.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("用户注册请求: username={}, role={}", request.getUsername(), request.getRole());
        
        // 验证用户名
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        
        // 验证密码
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        if (request.getPassword().length() < 6) {
            throw new BusinessException("密码长度至少6个字符");
        }
        
        // 验证角色
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new BusinessException("请选择角色");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            log.warn("注册失败: 用户名已存在 - {}", request.getUsername());
            throw new BusinessException("用户名已存在");
        }
        
        // 处理邮箱：如果为空字符串或null，设置为null
        String email = request.getEmail();
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
        
        // 如果提供了邮箱，检查是否已被使用
        if (email != null && userRepository.existsByEmail(email)) {
            log.warn("注册失败: 邮箱已被使用 - {}", email);
            throw new BusinessException("邮箱已被使用");
        }
        
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(email);
        user.setRole(request.getRole().toUpperCase());
        
        user = userRepository.save(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }
    
    /**
     * 用户登录
     */
    public AuthResponse login(LoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("登录失败: 用户不存在 - {}", request.getUsername());
                    return new BusinessException("用户名或密码错误");
                });
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败: 密码错误 - {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Integer id) {
        log.debug("查询用户: userId={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("用户不存在: userId={}", id);
                    return new BusinessException("用户不存在");
                });
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        log.debug("查询所有用户");
        return userRepository.findAll();
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Integer id, User userDetails) {
        log.info("更新用户信息: userId={}", id);
        User user = getUserById(id);
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getAvatar() != null) {
            user.setAvatar(userDetails.getAvatar());
        }
        User updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: userId={}", id);
        return updatedUser;
    }
}

