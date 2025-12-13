package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.ResearchPatentApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResearchPatentApprovalMapper extends BaseMapper<ResearchPatentApproval> {

    List<ResearchPatentApproval> selectApprovalList(@Param("patentId") Long patentId);
}

