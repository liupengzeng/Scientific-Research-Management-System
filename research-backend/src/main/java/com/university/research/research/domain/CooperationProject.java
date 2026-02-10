package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cooperation_project")
public class CooperationProject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "coop_project_id", type = IdType.AUTO)
    private Long coopProjectId;

    private String projectNo;

    private String projectName;

    private Long enterpriseId;

    private Long teacherId;

    private BigDecimal budget;

    private String status;

    private Integer currentStep;

    private LocalDateTime createTime;
}
