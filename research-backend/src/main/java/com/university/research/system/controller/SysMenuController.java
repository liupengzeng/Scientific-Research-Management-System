package com.university.research.system.controller;

import com.university.research.common.core.AjaxResult;
import com.university.research.system.domain.SysMenu;
import com.university.research.system.domain.SysUser;
import com.university.research.system.domain.vo.RoleMenuAssignRequest;
import com.university.research.system.service.SysMenuService;
import com.university.research.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "菜单管理", description = "菜单相关接口")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysUserService userService;

    /**
     * 获取菜单列表（根据当前用户角色，最小实现：无角色返回全部）
     */
    @Operation(summary = "获取当前用户菜单列表")
    @GetMapping("/list")
    public AjaxResult<List<SysMenu>> list() {
        // 获取当前用户的角色ID集合（最小实现从用户信息中取roleIds）
        // 如果后续有更完善的权限体系，可改为从SecurityContext中取
        List<Long> roleIds = new ArrayList<>();
        try {
            // 读取当前登录用户再取其完整角色（容错：若获取失败则返回全部菜单）
            SysUser current = userService.selectUserByUsername(com.university.research.common.utils.SecurityUtils.getUsername());
            if (current != null) {
                SysUser full = userService.selectUserById(current.getUserId());
                if (full != null && full.getRoles() != null) {
                    roleIds = full.getRoles().stream().map(r -> r.getRoleId()).collect(Collectors.toList());
                }
            }
        } catch (Exception ignore) {}
        List<SysMenu> tree = menuService.selectMenuTreeByRoleIds(roleIds);
        return AjaxResult.success(tree);
    }

    @Operation(summary = "查询菜单列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/manage/list")
    public AjaxResult<List<SysMenu>> manageList(SysMenu menu) {
        return AjaxResult.success(menuService.selectMenuList(menu));
    }

    @Operation(summary = "查询菜单树（全部）")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/manage/tree")
    public AjaxResult<List<SysMenu>> manageTree() {
        return AjaxResult.success(menuService.selectAllMenuTree());
    }

    @Operation(summary = "获取菜单详情")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{menuId}")
    public AjaxResult<SysMenu> get(@PathVariable Long menuId) {
        return AjaxResult.success(menuService.getById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public AjaxResult<Void> add(@RequestBody SysMenu menu) {
        menuService.create(menu);
        return AjaxResult.success();
    }

    @Operation(summary = "修改菜单")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public AjaxResult<Void> edit(@RequestBody SysMenu menu) {
        menuService.update(menu);
        return AjaxResult.success();
    }

    @Operation(summary = "删除菜单")
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public AjaxResult<Void> remove(@PathVariable Long menuId) {
        menuService.remove(menuId);
        return AjaxResult.success();
    }

    @Operation(summary = "修改菜单状态")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult<Void> changeStatus(@RequestBody SysMenu menu) {
        menuService.changeStatus(menu.getMenuId(), menu.getStatus());
        return AjaxResult.success();
    }

    @Operation(summary = "获取角色已分配的菜单ID")
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/roleMenuIds/{roleId}")
    public AjaxResult<List<Long>> roleMenuIds(@PathVariable Long roleId) {
        return AjaxResult.success(menuService.selectMenuIdsByRoleId(roleId));
    }

    @Operation(summary = "为角色分配菜单")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PostMapping("/assignRoleMenu")
    public AjaxResult<Void> assignRoleMenu(@RequestBody RoleMenuAssignRequest request) {
        menuService.assignMenusToRole(request.getRoleId(), request.getMenuIds());
        return AjaxResult.success();
    }
}
