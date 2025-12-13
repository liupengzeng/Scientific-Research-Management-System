package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("research_patent_approval")
public class ResearchPatentApproval implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long patentId; // 专利ID

    private String decision; // 审批决定(approve:通过 reject:驳回)

    private String comment; // 审批意见

    private Long approverId; // 审批人ID

    private String approverName; // 审批人名称

    private Integer finalFlag; // 是否终审(1是 0否)

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

