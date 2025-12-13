package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchProjectMemberMapper extends BaseMapper<ResearchProjectMember> {

    List<ResearchProjectMember> selectMemberList(@Param("projectId") Long projectId);
}

