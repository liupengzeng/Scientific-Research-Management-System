package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectFinal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchProjectFinalMapper extends BaseMapper<ResearchProjectFinal> {

    List<ResearchProjectFinal> selectFinalList(Page<ResearchProjectFinal> page, @Param("projectId") Long projectId);
}

