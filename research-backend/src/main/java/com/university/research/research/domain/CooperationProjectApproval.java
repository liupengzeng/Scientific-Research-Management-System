package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cooperation_project_approval")
public class CooperationProjectApproval implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "approval_id", type = IdType.AUTO)
    private Long approvalId;

    private Long coopProjectId;

    private Integer stepNo;

    private Long approverId;

    private String approverRole;

    private String decision;

    private String comment;

    private Integer finalFlag;

    private LocalDateTime createTime;
}
