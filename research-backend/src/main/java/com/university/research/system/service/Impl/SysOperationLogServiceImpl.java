package com.university.research.system.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.system.domain.SysOperationLog;
import com.university.research.system.mapper.SysOperationLogMapper;
import com.university.research.system.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysOperationLogServiceImpl implements SysOperationLogService {

    @Autowired
    private SysOperationLogMapper logMapper;

    @Override
    public int insert(SysOperationLog log) {
        return logMapper.insertLog(log);
    }

    @Override
    public Page<SysOperationLog> selectLogPage(Page<SysOperationLog> page, SysOperationLog query) {
        List<SysOperationLog> list = logMapper.selectLogList(page, query);
        page.setRecords(list);
        return page;
    }

    @Override
    public SysOperationLog selectById(Long logId) {
        return logMapper.selectById(logId);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return logMapper.deleteByIds(ids);
    }
}