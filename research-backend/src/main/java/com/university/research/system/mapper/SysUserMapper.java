package com.university.research.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.research.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author system
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（包含部门信息）
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectUserByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询用户（包含部门和角色信息）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser selectUserById(@Param("userId") Long userId);

    /**
     * 查询用户列表（包含部门和角色信息）
     *
     * @param user 用户查询条件
     * @return 用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param userId   用户ID（用于排除自己，新增时传null）
     * @return 数量
     */
    int checkUsernameUnique(@Param("username") String username, @Param("userId") Long userId);

    /**
     * 检查邮箱是否存在
     *
     * @param email  邮箱
     * @param userId 用户ID（用于排除自己，新增时传null）
     * @return 数量
     */
    int checkEmailUnique(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 检查手机号是否存在
     *
     * @param phone  手机号
     * @param userId 用户ID（用于排除自己，新增时传null）
     * @return 数量
     */
    int checkPhoneUnique(@Param("phone") String phone, @Param("userId") Long userId);
}

