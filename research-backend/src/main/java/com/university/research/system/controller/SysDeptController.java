package com.university.research.system.controller;

import com.university.research.common.core.AjaxResult;
import com.university.research.system.domain.SysDept;
import com.university.research.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 * 
 * @author system
 */
@Tag(name = "部门管理", description = "部门管理相关接口")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService deptService;

    /**
     * 查询部门列表（仅返回正常状态的部门）
     */
    @Operation(summary = "查询部门列表")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult<List<SysDept>> list() {
        List<SysDept> deptList = deptService.selectDeptList();
        return AjaxResult.success(deptList);
    }

    /**
     * 查询部门树
     */
    @Operation(summary = "查询部门树")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/tree")
    public AjaxResult<List<SysDept>> tree() {
        return AjaxResult.success(deptService.selectDeptTree());
    }

    /**
     * 获取部门详情
     */
    @Operation(summary = "获取部门详情")
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{deptId}")
    public AjaxResult<SysDept> get(@PathVariable Long deptId) {
        return AjaxResult.success(deptService.getById(deptId));
    }

    /**
     * 新增部门
     */
    @Operation(summary = "新增部门")
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public AjaxResult<Void> add(@RequestBody SysDept dept) {
        deptService.create(dept);
        return AjaxResult.success();
    }

    /**
     * 修改部门
     */
    @Operation(summary = "修改部门")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public AjaxResult<Void> edit(@RequestBody SysDept dept) {
        deptService.update(dept);
        return AjaxResult.success();
    }

    /**
     * 删除部门（无子部门时才允许删除）
     */
    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public AjaxResult<Void> remove(@PathVariable Long deptId) {
        int rows = deptService.remove(deptId);
        if (rows == 0) {
            return AjaxResult.error("该部门存在子部门，无法删除");
        }
        return AjaxResult.success();
    }

    /**
     * 修改部门状态
     */
    @Operation(summary = "修改部门状态")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult<Void> changeStatus(@RequestBody SysDept dept) {
        deptService.changeStatus(dept.getDeptId(), dept.getStatus());
        return AjaxResult.success();
    }

}

