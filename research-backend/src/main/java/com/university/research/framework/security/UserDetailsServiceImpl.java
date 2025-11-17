package com.university.research.framework.security;


import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 * 从数据库加载用户信息和权限
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;



    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户信息
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态
        if ("1".equals(user.getStatus())) {
            throw new RuntimeException("用户已被停用");
        }

        // 查询用户角色
        SysUser fullUser = userService.selectUserById(user.getUserId());
        // 调试：打印角色信息
        System.out.println("用户ID: " + fullUser.getUserId());
        System.out.println("用户名: " + fullUser.getUsername());
        if (fullUser.getRoles() != null) {
            System.out.println("角色数量: " + fullUser.getRoles().size());
            fullUser.getRoles().forEach(role -> {
                System.out.println("角色: " + role.getRoleName() + ", Key: " + role.getRoleKey());
            });
        } else {
            System.out.println("角色列表为空");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (fullUser.getRoles() != null && !fullUser.getRoles().isEmpty()) {
            // 将角色转换为权限
            authorities = fullUser.getRoles().stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleKey()))
//                    .collect(Collectors.toList());
                    .flatMap(role -> {
                        // 添加角色权限
                        List<GrantedAuthority> roleAuthorities = new ArrayList<>();
                        roleAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleKey()));

                        // 根据角色添加对应的权限字符串
                        if ("admin".equals(role.getRoleKey())) {
                            // 为管理员添加所有系统权限
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:query"));
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:add"));
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:edit"));
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:remove"));
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:list"));
                            // 添加其他需要的权限...
                        } else if ("user".equals(role.getRoleKey())) {
                            // 为普通用户添加基础权限
                            roleAuthorities.add(new SimpleGrantedAuthority("system:user:query"));
                        }

                        return roleAuthorities.stream();
                    })
                    .collect(Collectors.toList());
        }

        System.out.println("最终权限列表: " + authorities);
        // 创建Spring Security用户对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );

    }
}

