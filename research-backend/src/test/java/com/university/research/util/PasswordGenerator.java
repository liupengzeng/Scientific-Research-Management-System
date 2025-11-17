package com.university.research.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成BCrypt加密的密码，用于测试数据插入
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成默认密码123456的BCrypt加密
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密后: " + encodedPassword);
        System.out.println();
        System.out.println("SQL插入语句:");
        System.out.println("INSERT INTO sys_user (username, password, real_name, user_type, status, create_by) VALUES");
        System.out.println("('admin', '" + encodedPassword + "', '系统管理员', '00', '0', 'admin');");
    }

}

