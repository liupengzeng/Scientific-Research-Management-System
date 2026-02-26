package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.CooperationProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CooperationProjectMapper extends BaseMapper<CooperationProject> {

    List<CooperationProject> selectList(Page<CooperationProject> page, @Param("q") CooperationProject query);
}
