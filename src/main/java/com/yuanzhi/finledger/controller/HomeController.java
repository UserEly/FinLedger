package com.yuanzhi.finledger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 提供系统基本信息
 */
@RestController
public class HomeController {
    
    /**
     * 系统首页
     * 返回系统基本信息
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "FinLedger 财务管理系统");
        info.put("version", "1.0.0");
        info.put("status", "running");
        info.put("message", "后端服务运行正常，请访问前端页面 http://localhost:5173");
        info.put("api", "http://localhost:8080/api");
        return ResponseEntity.ok(info);
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "服务运行正常");
        return ResponseEntity.ok(status);
    }
}


