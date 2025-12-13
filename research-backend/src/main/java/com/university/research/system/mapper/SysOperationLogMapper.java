package com.university.research.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.system.domain.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    List<SysOperationLog> selectLogList(Page<SysOperationLog> page, @Param("log") SysOperationLog log);

    SysOperationLog selectById(@Param("logId") Long logId);

    int insertLog(SysOperationLog log);

    int deleteByIds(@Param("ids") List<Long> ids);
}