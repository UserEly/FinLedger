package com.yuanzhi.finledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 支持会计、财务经理、老板三种角色
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50, unique = true)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(nullable = false, length = 20)
    private String role; // ACCOUNTANT, MANAGER, BOSS
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(length = 255)
    private String avatar;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}


