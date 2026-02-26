package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.research.domain.CooperationProjectApproval;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CooperationProjectApprovalMapper extends BaseMapper<CooperationProjectApproval> {

    List<CooperationProjectApproval> selectByProjectId(@Param("coopProjectId") Long coopProjectId);
}
