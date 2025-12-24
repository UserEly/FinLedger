package com.yuanzhi.finledger.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 安全工具类
 * 获取当前登录用户信息
 */
public class SecurityUtil {
    
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * 获取当前用户角色
     */
    public static String getCurrentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null && !authorities.isEmpty()) {
                String authority = authorities.iterator().next().getAuthority();
                // 移除 "ROLE_" 前缀
                if (authority.startsWith("ROLE_")) {
                    return authority.substring(5);
                }
                return authority;
            }
        }
        return null;
    }
    
    /**
     * 检查当前用户是否有指定角色
     */
    public static boolean hasRole(String role) {
        String currentRole = getCurrentRole();
        return role != null && role.equals(currentRole);
    }
    
    /**
     * 检查当前用户是否是会计
     */
    public static boolean isAccountant() {
        return hasRole(RoleConstants.ACCOUNTANT);
    }
    
    /**
     * 检查当前用户是否是财务经理
     */
    public static boolean isManager() {
        return hasRole(RoleConstants.MANAGER);
    }
    
    /**
     * 检查当前用户是否是老板
     */
    public static boolean isBoss() {
        return hasRole(RoleConstants.BOSS);
    }
    
    /**
     * 检查当前用户是否是财务经理或老板
     */
    public static boolean isManagerOrBoss() {
        return isManager() || isBoss();
    }
}


