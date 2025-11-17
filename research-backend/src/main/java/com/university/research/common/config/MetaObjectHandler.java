package com.university.research.common.config;

import com.university.research.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus自动填充处理器
 * 用于自动填充创建时间、更新时间、创建者、更新者等字段
 */
@Component
public class MetaObjectHandler implements com.baomidou.mybatisplus.core.handlers.MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 自动填充创建者（如果已登录）
        try {
            String username = SecurityUtils.getUsername();
            if (username != null && !username.isEmpty()) {
                this.strictInsertFill(metaObject, "createBy", String.class, username);
                this.strictInsertFill(metaObject, "updateBy", String.class, username);
            }
        } catch (Exception e) {
            // 如果SecurityUtils未初始化或未登录，忽略
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 自动填充更新者（如果已登录）
        try {
            String username = SecurityUtils.getUsername();
            if (username != null && !username.isEmpty()) {
                this.strictUpdateFill(metaObject, "updateBy", String.class, username);
            }
        } catch (Exception e) {
            // 如果SecurityUtils未初始化或未登录，忽略
        }
    }
}

