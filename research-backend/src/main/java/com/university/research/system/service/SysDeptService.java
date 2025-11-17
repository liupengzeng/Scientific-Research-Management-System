package com.university.research.system.service;

import com.university.research.system.domain.SysDept;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author system
 */
public interface SysDeptService {

    /**
     * 查询部门列表（仅查询正常状态的部门）
     *
     * @return 部门列表
     */
    List<SysDept> selectDeptList();

    /**
     * 查询部门树（返回树形结构）
     */
    List<SysDept> selectDeptTree();

    /**
     * 根据ID获取详情
     */
    SysDept getById(Long deptId);

    /**
     * 新增
     */
    int create(SysDept dept);

    /**
     * 修改
     */
    int update(SysDept dept);

    /**
     * 删除（若存在子部门则不允许删除，返回0）
     */
    int remove(Long deptId);

    /**
     * 修改状态
     */
    int changeStatus(Long deptId, String status);
}

