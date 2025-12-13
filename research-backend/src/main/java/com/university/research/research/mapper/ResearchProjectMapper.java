package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchProjectMapper extends BaseMapper<ResearchProject> {

    List<ResearchProject> selectProjectList(Page<ResearchProject> page, @Param("p") ResearchProject query);
}

