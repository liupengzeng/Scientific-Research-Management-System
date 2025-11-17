package com.university.research.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联Mapper接口
 * 
 * @author system
 */
@Mapper
public interface SysUserRoleMapper {

    /**
     * 插入用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户角色关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteUserRoleByRoleId(@Param("roleId") Long roleId);
}

