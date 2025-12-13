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
@TableName("research_book")
public class ResearchBook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "book_id", type = IdType.AUTO)
    private Long bookId;

    private String bookTitle; // 书名

    private String authors; // 作者列表（按顺序，逗号分隔）

    private String publisher; // 出版社

    private LocalDate publishDate; // 出版日期

    private String isbn; // ISBN号

    private Integer pageCount; // 页数

    private Integer wordCount; // 字数（万字）

    private String bookType; // 著作类型（专著、译著、教材、工具书、其他）

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

