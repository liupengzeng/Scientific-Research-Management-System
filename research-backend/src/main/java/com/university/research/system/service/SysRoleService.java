package com.university.research.system.service;

import com.university.research.system.domain.SysRole;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author system
 */
public interface SysRoleService {

    /**
     * 查询角色列表（可带筛选条件）
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据 ID 查询角色详情
     */
    SysRole getById(Long roleId);

    /**
     * 新增角色
     */
    int create(SysRole role);

    /**
     * 修改角色
     */
    int update(SysRole role);

    /**
     * 删除角色（未绑定用户时）
     */
    int remove(Long roleId);

    /**
     * 修改角色状态
     */
    int changeStatus(Long roleId, String status);

    /**
     * 检查角色名称唯一
     */
    boolean checkRoleNameUnique(String roleName, Long roleId);

    /**
     * 检查角色权限标识唯一
     */
    boolean checkRoleKeyUnique(String roleKey, Long roleId);
}

