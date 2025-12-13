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
@TableName("research_award")
public class ResearchAward implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "award_id", type = IdType.AUTO)
    private Long awardId;

    private String awardName; // 获奖名称

    private String awardees; // 获奖人/团队（按顺序，逗号分隔）

    private String awardLevel; // 获奖等级（一等奖、二等奖、三等奖）

    private String awardType; // 获奖类型（国家级、省部级、校级）

    private LocalDate awardDate; // 获奖日期

    private String awardingUnit; // 颁奖单位

    private String certificateNo; // 证书编号

    private BigDecimal bonus; // 奖金（万元）

    private String attachmentPath; // 附件路径

    private String status; // 状态（active:有效 inactive:无效）

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String remark; // 备注

    // 查询条件字段（不映射到数据库）
    @TableField(exist = false)
    private String beginTime; // 开始时间（用于日期范围查询）

    @TableField(exist = false)
    private String endTime; // 结束时间（用于日期范围查询）
}

