package com.university.research.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.common.annotation.Log;
import com.university.research.system.domain.SysOperationLog;
import com.university.research.system.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "操作日志", description = "操作日志查询与管理")
@RestController
@RequestMapping("/system/operationLog")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService logService;

    @Operation(summary = "日志列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:operationLog:list')")
    public AjaxResult<PageResult<SysOperationLog>> list(SysOperationLog query, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<SysOperationLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<SysOperationLog> result = logService.selectLogPage(page, query);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "日志详情")
    @GetMapping("/{logId}")
    @PreAuthorize("hasAuthority('system:operationLog:query')")
    public AjaxResult<SysOperationLog> getInfo(@PathVariable Long logId) {
        return AjaxResult.success(logService.selectById(logId));
    }

    @Operation(summary = "删除日志")
    @DeleteMapping("/{logIds}")
    @PreAuthorize("hasAuthority('system:operationLog:remove')")
    @Log(title = "删除操作日志", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] logIds) {
        List<Long> ids = Arrays.asList(logIds);
        logService.deleteByIds(ids);
        return AjaxResult.success();
    }
}