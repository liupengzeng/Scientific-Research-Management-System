package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchPaperMapper extends BaseMapper<ResearchPaper> {

    List<ResearchPaper> selectPaperList(@Param("paper") ResearchPaper paper);
}

