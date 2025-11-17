package com.university.research.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.system.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRoleList(SysRole role);

    SysRole selectRoleById(Long roleId);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleById(Long roleId);

    int updateStatus(@Param("roleId") Long roleId, @Param("status") String status);

    int countUserRole(Long roleId);

    int checkRoleNameUnique(@Param("roleName") String roleName, @Param("roleId") Long roleId);

    int checkRoleKeyUnique(@Param("roleKey") String roleKey, @Param("roleId") Long roleId);
}

