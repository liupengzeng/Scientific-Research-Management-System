package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("research_project_approval")
public class ResearchProjectApproval implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private String decision; // approve / reject

    private String comment;

    private Long approverId;

    private String approverName;

    private Integer finalFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

