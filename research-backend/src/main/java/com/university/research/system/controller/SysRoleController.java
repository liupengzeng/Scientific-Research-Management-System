package com.university.research.system.controller;

import com.university.research.common.core.AjaxResult;
import com.university.research.system.domain.SysRole;
import com.university.research.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Operation(summary = "查询角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public AjaxResult<List<SysRole>> list(SysRole role) {
        return AjaxResult.success(roleService.selectRoleList(role));
    }

    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{roleId}")
    public AjaxResult<SysRole> get(@PathVariable Long roleId) {
        return AjaxResult.success(roleService.getById(roleId));
    }

    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public AjaxResult<Void> add(@RequestBody SysRole role) {
        roleService.create(role);
        return AjaxResult.success();
    }

    @Operation(summary = "修改角色")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public AjaxResult<Void> edit(@RequestBody SysRole role) {
        roleService.update(role);
        return AjaxResult.success();
    }

    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{roleId}")
    public AjaxResult<Void> remove(@PathVariable Long roleId) {
        roleService.remove(roleId);
        return AjaxResult.success();
    }

    @Operation(summary = "修改角色状态")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult<Void> changeStatus(@RequestBody SysRole role) {
        roleService.changeStatus(role.getRoleId(), role.getStatus());
        return AjaxResult.success();
    }
}

