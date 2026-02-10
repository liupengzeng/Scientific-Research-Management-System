package com.university.research.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.CooperationEnterprise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CooperationEnterpriseMapper extends BaseMapper<CooperationEnterprise> {

    List<CooperationEnterprise> selectList(Page<CooperationEnterprise> page, @Param("q") CooperationEnterprise query);
}
