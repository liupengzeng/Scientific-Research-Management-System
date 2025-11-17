package com.university.research.framework.web.controller;

import com.university.research.common.core.AjaxResult;
import com.university.research.common.exception.ServiceException;
import com.university.research.common.utils.SecurityUtils;
import com.university.research.framework.security.JwtTokenUtil;

import com.university.research.system.domain.SysRole;
import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysMenuService;
import com.university.research.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 处理登录、登出、获取用户信息等认证相关接口
 * 
 * @author system
 */
@Tag(name = "认证管理", description = "登录、登出、用户信息等认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public AjaxResult<Map<String, Object>> login(@RequestBody Map<String, String> loginForm, HttpServletRequest request) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            throw new ServiceException("用户名和密码不能为空");
        }

        try {
            // 创建认证令牌
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            // 执行认证（Spring Security会自动验证用户名密码）
            authenticationManager.authenticate(authenticationToken);

            // 认证成功，生成JWT Token
            SysUser user = userService.selectUserByUsername(username);
            if (user == null) {
                throw new ServiceException("用户不存在");
            }

            // 检查用户状态
            if ("1".equals(user.getStatus())) {
                throw new ServiceException("用户已被停用，请联系管理员");
            }

            // 生成Token
            String token = jwtTokenUtil.generateToken(username, user.getUserId());

            // 获取用户完整信息（包含角色）
            SysUser fullUser = userService.selectUserById(user.getUserId());
            List<Long> roleIds = fullUser.getRoles() != null
                    ? fullUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList())
                    : List.of();

            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("deptId", user.getDeptId());
            userInfo.put("deptName", user.getDept() != null ? user.getDept().getDeptName() : null);
            userInfo.put("roles", roleIds);
            List<String> permissions = menuService.selectPermsByRoleIds(roleIds);
            userInfo.put("permissions", permissions);

            // 更新最后登录时间和IP
            updateLoginInfo(user, getClientIp(request));

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", userInfo);

            return AjaxResult.success("登录成功", result);
        } catch (BadCredentialsException e) {
            throw new ServiceException("用户名或密码错误");
        } catch (Exception e) {
            throw new ServiceException("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/userInfo")
    public AjaxResult<Map<String, Object>> getUserInfo() {
        try {
            String username = SecurityUtils.getUsername();
            if (username == null) {
                return AjaxResult.error(401, "未登录");
            }

            SysUser user = userService.selectUserByUsername(username);
            if (user == null) {
                return AjaxResult.error(401, "用户不存在");
            }

            // 获取完整用户信息（包含角色）
            SysUser fullUser = userService.selectUserById(user.getUserId());

            List<Long> roleIds = fullUser.getRoles() != null
                    ? fullUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList())
                    : List.of();
            List<String> permissions = menuService.selectPermsByRoleIds(roleIds);

            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", fullUser.getUserId());
            userInfo.put("username", fullUser.getUsername());
            userInfo.put("realName", fullUser.getRealName());
            userInfo.put("avatar", fullUser.getAvatar());
            userInfo.put("deptId", fullUser.getDeptId());
            userInfo.put("deptName", fullUser.getDept() != null ? fullUser.getDept().getDeptName() : null);
            userInfo.put("roles", roleIds);
            userInfo.put("permissions", permissions);

            return AjaxResult.success(userInfo);
        } catch (Exception e) {
            return AjaxResult.error(401, "获取用户信息失败");
        }
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public AjaxResult<Void> logout() {
        SecurityContextHolder.clearContext();
        return AjaxResult.success("登出成功");
    }

    /**
     * 更新登录信息
     */
    private void updateLoginInfo(SysUser user, String ip) {
        // 只更新登录IP和时间，不更新其他字段
        SysUser updateUser = new SysUser();
        updateUser.setUserId(user.getUserId());
        updateUser.setLastLoginIp(ip);
        updateUser.setLastLoginTime(LocalDateTime.now());
        // 直接使用Mapper更新，避免触发密码加密等逻辑
        userService.updateLoginInfo(user.getUserId(),ip,LocalDateTime.now());
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

