package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchPatent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchPatentMapper extends BaseMapper<ResearchPatent> {

    List<ResearchPatent> selectPatentList(@Param("patent") ResearchPatent patent);
}

