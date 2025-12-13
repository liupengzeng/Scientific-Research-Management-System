package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_project_member")
public class ResearchProjectMember implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long userId;

    private String memberRole; // 主持人、主要参与人、普通参与人等

    private BigDecimal workloadRatio; // 工作量比例(%)

    private String responsibility; // 承担任务

    private LocalDate joinDate; // 加入日期

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联查询字段（不映射到数据库）
    @TableField(exist = false)
    private String username; // 用户名

    @TableField(exist = false)
    private String realName; // 真实姓名

    @TableField(exist = false)
    private String deptName; // 部门名称
}

