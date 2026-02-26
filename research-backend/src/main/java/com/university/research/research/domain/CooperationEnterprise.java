package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cooperation_enterprise")
public class CooperationEnterprise implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "enterprise_id", type = IdType.AUTO)
    private Long enterpriseId;

    private String enterpriseName;

    private String creditCode;

    private String industry;

    private String contactName;

    private String contactPhone;

    private String status;

    private LocalDateTime createTime;
}
