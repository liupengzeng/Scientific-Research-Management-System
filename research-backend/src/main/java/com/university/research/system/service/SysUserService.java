package com.university.research.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.system.domain.SysUser;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author system
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectUserByUsername(String username);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser selectUserById(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param page 分页对象
     * @param user 查询条件
     * @return 用户列表
     */
    Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user);

    /**
     * 查询用户列表
     *
     * @param user 查询条件
     * @return 用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUser user);

    /**
     * 删除用户
     *
     * @param userIds 用户ID数组
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 新密码
     * @return 结果
     */
    int resetUserPassword(Long userId, String password);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 结果
     */
    int changeStatus(Long userId, String status);

    /**
     * 检查用户名是否唯一
     *
     * @param username 用户名
     * @param userId   用户ID（用于排除自己）
     * @return 是否唯一
     */
    boolean checkUsernameUnique(String username, Long userId);

    /**
     * 检查邮箱是否唯一
     *
     * @param email  邮箱
     * @param userId 用户ID（用于排除自己）
     * @return 是否唯一
     */
    boolean checkEmailUnique(String email, Long userId);

    /**
     * 检查手机号是否唯一
     *
     * @param phone  手机号
     * @param userId 用户ID（用于排除自己）
     * @return 是否唯一
     */
    boolean checkPhoneUnique(String phone, Long userId);
}

