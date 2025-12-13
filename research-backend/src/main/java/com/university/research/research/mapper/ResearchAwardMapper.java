package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchAwardMapper extends BaseMapper<ResearchAward> {

    List<ResearchAward> selectAwardList(@Param("award") ResearchAward award);
}

