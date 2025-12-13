package com.university.research.research.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("research_patent")
public class ResearchPatent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "patent_id", type = IdType.AUTO)
    private Long patentId;

    private String patentName; // 专利名称

    private String inventors; // 发明人列表

    private String patentType; // 专利类型（发明专利、实用新型等）

    private String patentNo; // 专利号

    private LocalDate applicationDate; // 申请日期

    private LocalDate authorizationDate; // 授权日期

    private LocalDate validUntil; // 有效期至

    private String patentStatus; // 专利状态（申请中、已授权、已转化等）

    private String attachmentPath; // 附件路径

    private Long projectId; // 关联项目ID

    private String status; // 状态（draft:草稿 submitted:已提交 approved:已审核 rejected:已驳回）

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

    // 关联查询字段（不映射到数据库）
    @TableField(exist = false)
    private String projectName; // 项目名称
}

