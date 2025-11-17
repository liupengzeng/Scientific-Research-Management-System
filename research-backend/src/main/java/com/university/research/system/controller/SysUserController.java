package com.university.research.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.system.domain.SysUser;
import com.university.research.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户管理控制器
 * 
 * @author system
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public AjaxResult<PageResult<SysUser>> list(SysUser user, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<SysUser> result = userService.selectUserPage(page, user);
        return AjaxResult.page(PageResult.of(result));
    }

    /**
     * 根据用户ID查询用户详情
     */
    @Operation(summary = "查询用户详情")
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public AjaxResult<SysUser> getInfo(@PathVariable Long userId) {
        SysUser user = userService.selectUserById(userId);
        return AjaxResult.success(user);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public AjaxResult<Void> add(@Validated @RequestBody SysUser user) {
        userService.insertUser(user);
        return AjaxResult.success();
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public AjaxResult<Void> edit(@Validated @RequestBody SysUser user) {
        userService.updateUser(user);
        return AjaxResult.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult<Void> remove(@PathVariable Long[] userIds) {
        userService.deleteUserByIds(userIds);
        return AjaxResult.success();
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置用户密码")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public AjaxResult<Void> resetPwd(@RequestBody SysUser user) {
        userService.resetUserPassword(user.getUserId(), user.getPassword());
        return AjaxResult.success();
    }

    /**
     * 修改用户状态
     */
    @Operation(summary = "修改用户状态")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult<Void> changeStatus(@RequestBody SysUser user) {
        userService.changeStatus(user.getUserId(), user.getStatus());
        return AjaxResult.success();
    }

    /**
     * 检查用户名是否唯一
     */
    @Operation(summary = "检查用户名是否唯一")
    @GetMapping("/checkUsernameUnique")
    public AjaxResult<Boolean> checkUsernameUnique(@RequestParam String username, @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkUsernameUnique(username, userId);
        return AjaxResult.success(unique);
    }

    /**
     * 检查邮箱是否唯一
     */
    @Operation(summary = "检查邮箱是否唯一")
    @GetMapping("/checkEmailUnique")
    public AjaxResult<Boolean> checkEmailUnique(@RequestParam String email, @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkEmailUnique(email, userId);
        return AjaxResult.success(unique);
    }

    /**
     * 检查手机号是否唯一
     */
    @Operation(summary = "检查手机号是否唯一")
    @GetMapping("/checkPhoneUnique")
    public AjaxResult<Boolean> checkPhoneUnique(@RequestParam String phone, @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkPhoneUnique(phone, userId);
        return AjaxResult.success(unique);
    }
}

