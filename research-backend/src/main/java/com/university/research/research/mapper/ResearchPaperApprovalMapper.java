package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchPaperApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchPaperApprovalMapper extends BaseMapper<ResearchPaperApproval> {

    List<ResearchPaperApproval> selectApprovalList(@Param("paperId") Long paperId);
}

