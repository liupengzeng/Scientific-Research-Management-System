package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_project")
public class ResearchProject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "project_id", type = IdType.AUTO)
    private Long projectId;

    private String projectNo;

    private String projectName;

    private Long projectTypeId;

    private Long leaderId;

    private Long deptId;

    private String sourceUnit;

    private String projectLevel;

    private BigDecimal totalBudget;

    private BigDecimal approvedAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer duration;

    private String projectStatus;

    private String keywords;

    private String researchContent;

    private String expectedResult;

    private String currentProgress;

    private String approvalDocNo;

    private LocalDate approvalDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private LocalDate beginDate;

    @TableField(exist = false)
    private LocalDate endDateQuery;
}

