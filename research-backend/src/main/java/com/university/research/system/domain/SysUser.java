package com.university.research.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 
 * @author system
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户类型（00系统用户 01教师）
     */
    private String userType;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 职称
     */
    private String title;

    /**
     * 研究方向
     */
    private String researchDirection;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门信息（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private SysDept dept;

    /**
     * 角色列表（关联查询，不映射到数据库）
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 角色ID列表（用于新增/修改时传递，不映射到数据库）
     */
    @TableField(exist = false)
    private List<Long> roleIds;
}

