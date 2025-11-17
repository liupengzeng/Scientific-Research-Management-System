package com.university.research.system.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.system.domain.SysUser;
import com.university.research.system.mapper.SysUserMapper;
import com.university.research.system.mapper.SysUserRoleMapper;
import com.university.research.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author system
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 默认密码
     */
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public SysUser selectUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
        List<SysUser> userList = userMapper.selectUserList(user);
        // 手动分页（因为使用了关联查询，MyBatis-Plus自动分页可能不准确）
        int total = userList.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);
        
        if (start < total) {
            List<SysUser> pageList = userList.subList(start, end);
            page.setRecords(pageList);
            page.setTotal(total);
        } else {
            page.setRecords(List.of());
            page.setTotal(total);
        }
        return page;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        // 检查用户名唯一性
        if (!checkUsernameUnique(user.getUsername(), null)) {
            throw new ServiceException("新增用户'" + user.getUsername() + "'失败，用户名已存在");
        }
        // 检查邮箱唯一性
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !checkEmailUnique(user.getEmail(), null)) {
            throw new ServiceException("新增用户'" + user.getUsername() + "'失败，邮箱已存在");
        }
        // 检查手机号唯一性
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !checkPhoneUnique(user.getPhone(), null)) {
            throw new ServiceException("新增用户'" + user.getUsername() + "'失败，手机号已存在");
        }
        
        // 密码加密
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 插入用户
        int rows = userMapper.insert(user);
        
        // 插入用户角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            insertUserRoles(user.getUserId(), user.getRoleIds());
        }
        
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        if (user.getUserId() == null) {
            throw new ServiceException("修改用户失败，用户ID不能为空");
        }
        Long userId = user.getUserId();
        
        // 检查用户名唯一性
        if (!checkUsernameUnique(user.getUsername(), userId)) {
            throw new ServiceException("修改用户'" + user.getUsername() + "'失败，用户名已存在");
        }
        // 检查邮箱唯一性
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !checkEmailUnique(user.getEmail(), userId)) {
            throw new ServiceException("修改用户'" + user.getUsername() + "'失败，邮箱已存在");
        }
        // 检查手机号唯一性
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !checkPhoneUnique(user.getPhone(), userId)) {
            throw new ServiceException("修改用户'" + user.getUsername() + "'失败，手机号已存在");
        }
        
        // 如果密码不为空，则加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 不更新密码 - 从数据库查询原密码
            SysUser existingUser = userMapper.selectById(user.getUserId());
            if (existingUser != null) {
                user.setPassword(existingUser.getPassword());
            }
        }
        
        // 更新用户
        int rows = userMapper.updateById(user);
        
        // 删除原有角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        
        // 插入新的角色关联
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            insertUserRoles(userId, user.getRoleIds());
        }
        
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        // 删除用户角色关联
        for (Long userId : userIds) {
            userRoleMapper.deleteUserRoleByUserId(userId);
        }
        // 删除用户（逻辑删除）
        return userMapper.deleteBatchIds(List.of(userIds));
    }

    @Override
    public int resetUserPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return userMapper.updateById(user);
    }

    @Override
    public int changeStatus(Long userId, String status) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setStatus(status);
        return userMapper.updateById(user);
    }

    @Override
    public boolean checkUsernameUnique(String username, Long userId) {
        int count = userMapper.checkUsernameUnique(username, userId);
        return count == 0;
    }

    @Override
    public boolean checkEmailUnique(String email, Long userId) {
        int count = userMapper.checkEmailUnique(email, userId);
        return count == 0;
    }

    @Override
    public boolean checkPhoneUnique(String phone, Long userId) {
        int count = userMapper.checkPhoneUnique(phone, userId);
        return count == 0;
    }

    /**
     * 插入用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void insertUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            userRoleMapper.insertUserRole(userId, roleId);
        }
    }

    @Override
    public int updateLoginInfo(Long userId, String ip, LocalDateTime time) {
        return userMapper.updateLoginInfo(userId, ip, time);
    }
}

