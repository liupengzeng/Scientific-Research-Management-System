package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchProjectApprovalMapper extends BaseMapper<ResearchProjectApproval> {

    List<ResearchProjectApproval> selectApprovalList(Page<ResearchProjectApproval> page, @Param("q") ResearchProjectApproval query);
}

