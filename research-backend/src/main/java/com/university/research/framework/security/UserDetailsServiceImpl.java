package com.university.research.framework.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现
 * 从数据库加载用户信息和权限
 * 
 * 注意：当前为基础实现，待User实体类和Mapper创建后需要完善
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 待User实体类和UserMapper创建后，从数据库查询用户信息
        // 当前实现抛出异常，提示需要先创建用户实体类
        
        throw new UsernameNotFoundException("用户实体类尚未创建，请先完成Phase 2.1用户管理模块");
        
        // 后续实现示例（仅供参考）：
        // SysUser user = userMapper.selectByUsername(username);
        // if (user == null) {
        //     throw new UsernameNotFoundException("用户不存在: " + username);
        // }
        // if ("1".equals(user.getStatus())) {
        //     throw new RuntimeException("用户已被停用");
        // }
        // 
        // // 查询用户角色和权限
        // List<String> permissions = permissionService.getUserPermissions(user.getUserId());
        // List<GrantedAuthority> authorities = permissions.stream()
        //         .map(SimpleGrantedAuthority::new)
        //         .collect(Collectors.toList());
        // 
        // return new org.springframework.security.core.userdetails.User(
        //         user.getUsername(),
        //         user.getPassword(),
        //         authorities
        // );
    }
}

