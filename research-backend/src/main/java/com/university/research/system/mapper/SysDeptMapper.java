package com.university.research.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.system.domain.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 部门Mapper接口
 * 
 * @author system
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表（仅查询正常状态的部门）
     *
     * @return 部门列表
     */
    List<SysDept> selectDeptList();

    /**
     * 查询部门树（全部或按状态）
     */
    List<SysDept> selectAll();

    /**
     * 根据ID获取详情
     */
    SysDept selectById(Long deptId);

    /**
     * 新增部门
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门（物理删除）
     */
    int deleteDeptById(Long deptId);

    /**
     * 统计子部门数量
     */
    int countChildren(Long deptId);

    /**
     * 修改状态
     */
    int updateStatus(@Param("deptId") Long deptId, @Param("status") String status);
}

