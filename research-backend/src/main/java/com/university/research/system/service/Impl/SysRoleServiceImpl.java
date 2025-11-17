package com.university.research.system.service.Impl;

import com.university.research.common.exception.ServiceException;
import com.university.research.system.domain.SysRole;
import com.university.research.system.mapper.SysRoleMapper;
import com.university.research.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public SysRole getById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    public int create(SysRole role) {
        validateUnique(role);
        if (role.getStatus() == null) {
            role.setStatus("0");
        }
        return roleMapper.insertRole(role);
    }

    @Override
    public int update(SysRole role) {
        validateUnique(role);
        return roleMapper.updateRole(role);
    }

    @Override
    public int remove(Long roleId) {
        int userCount = roleMapper.countUserRole(roleId);
        if (userCount > 0) {
            throw new ServiceException("角色已分配用户，无法删除");
        }
        return roleMapper.deleteRoleById(roleId);
    }

    @Override
    public int changeStatus(Long roleId, String status) {
        return roleMapper.updateStatus(roleId, status);
    }

    @Override
    public boolean checkRoleNameUnique(String roleName, Long roleId) {
        return roleMapper.checkRoleNameUnique(roleName, roleId) == 0;
    }

    @Override
    public boolean checkRoleKeyUnique(String roleKey, Long roleId) {
        return roleMapper.checkRoleKeyUnique(roleKey, roleId) == 0;
    }

    private void validateUnique(SysRole role) {
        Long roleId = role.getRoleId();
        if (!checkRoleNameUnique(role.getRoleName(), roleId)) {
            throw new ServiceException("角色名称已存在");
        }
        if (!checkRoleKeyUnique(role.getRoleKey(), roleId)) {
            throw new ServiceException("角色权限字符已存在");
        }
    }
}

