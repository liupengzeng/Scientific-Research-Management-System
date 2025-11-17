package com.university.research.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.system.domain.SysOperationLog;

import java.util.List;

public interface SysOperationLogService {

    int insert(SysOperationLog log);

    Page<SysOperationLog> selectLogPage(Page<SysOperationLog> page, SysOperationLog query);

    SysOperationLog selectById(Long logId);

    int deleteByIds(List<Long> ids);
}