package com.university.research.framework.security;

import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.warn("开始加载用户: {}", username);

        // 1. 查用户
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 2. 查状态
        if ("1".equals(user.getStatus())) {
            log.warn("用户被停用: {}", username);
            throw new RuntimeException("用户已被停用");
        }

        // 3. 查完整角色
        SysUser fullUser = userService.selectUserById(user.getUserId());
        log.warn("用户:{} 的角色列表: {}", username, fullUser.getRoles());

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (fullUser.getRoles() != null && !fullUser.getRoles().isEmpty()) {
            authorities = fullUser.getRoles().stream()
                    .flatMap(role -> {
                        List<GrantedAuthority> roleAuthorities = new ArrayList<>();
                        String roleKey = role.getRoleKey();
                        log.warn("处理角色: {} -> roleKey={}", role.getRoleName(), roleKey);

                        if (roleKey != null) {
                            // 1) 角色本身
                            String roleAuthority = "ROLE_" + roleKey.toUpperCase();
                            roleAuthorities.add(new SimpleGrantedAuthority(roleAuthority));
                            log.warn("  添加角色权限: {}", roleAuthority);

                            // 2) 根据角色硬编码细粒度权限
                            String roleKeyLower = roleKey.toLowerCase();
                            if ("admin".equals(roleKeyLower)) {
                                String[] adminPerms = {
                                        "system:user:query", "system:user:add", "system:user:edit",
                                        "system:user:remove", "system:user:list", "system:user:resetPwd",
                                        "system:dept:list", "system:role:list", "system:menu:list",
                                        "research:project:approve", "research:project:submit",
                                        "cooperation:enterprise:list", "cooperation:enterprise:query",
                                        "cooperation:enterprise:add", "cooperation:enterprise:edit", "cooperation:enterprise:remove",
                                        "cooperation:project:list", "cooperation:project:query", "cooperation:project:add",
                                        "cooperation:project:edit", "cooperation:project:submit", "cooperation:project:approve",
                                        "cooperation:project:approve:list", "cooperation:project:remove"
                                };
                                for (String p : adminPerms) {
                                    roleAuthorities.add(new SimpleGrantedAuthority(p));
                                    log.warn("  添加细粒度权限: {}", p);
                                }
                            } else if ("teacher".equals(roleKeyLower)) {
                                roleAuthorities.add(new SimpleGrantedAuthority("research:project:submit"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:add"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:edit"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:submit"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:list"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:query"));
                                log.warn("  添加细粒度权限: research/cooperation submit");
                            } else if ("research_group_leader".equals(roleKeyLower)
                                    || "research_office".equals(roleKeyLower)
                                    || "college_leader".equals(roleKeyLower)) {
                                roleAuthorities.add(new SimpleGrantedAuthority("research:project:approve"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:approve"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:approve:list"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:list"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:project:query"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:enterprise:list"));
                                roleAuthorities.add(new SimpleGrantedAuthority("cooperation:enterprise:query"));
                                log.warn("  添加细粒度权限: research/cooperation approve");
                            } else if ("user".equals(roleKeyLower)) {
                                roleAuthorities.add(new SimpleGrantedAuthority("system:user:query"));
                                log.warn("  添加细粒度权限: system:user:query");
                            } else {
                                log.warn("  未知角色，无额外细粒度权限");
                            }
                        }
                        return roleAuthorities.stream();
                    })
                    .collect(Collectors.toList());
        }

        log.warn("用户:{} 最终 authorities: {}", username, authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}