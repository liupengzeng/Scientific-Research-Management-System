package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchProjectType;
import com.university.research.research.service.ResearchProjectTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "项目类型管理", description = "项目类型 CRUD 接口")
@RestController
@RequestMapping("/api/research/projectType")
public class ResearchProjectTypeController {

    @Autowired
    private ResearchProjectTypeService typeService;

    @Operation(summary = "分页查询项目类型")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:projectType:list')")
    public AjaxResult<PageResult<ResearchProjectType>> list(ResearchProjectType query, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchProjectType> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchProjectType> result = typeService.selectPage(page, query);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "获取项目类型详情")
    @GetMapping("/{typeId}")
    @PreAuthorize("hasAuthority('research:projectType:query')")
    public AjaxResult<ResearchProjectType> getInfo(@PathVariable Long typeId) {
        return AjaxResult.success(typeService.selectById(typeId));
    }

    @Operation(summary = "新增项目类型")
    @PostMapping
    @PreAuthorize("hasAuthority('research:projectType:add')")
    @Log(title = "项目类型", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchProjectType type) {
        typeService.create(type);
        return AjaxResult.success();
    }

    @Operation(summary = "修改项目类型")
    @PutMapping
    @PreAuthorize("hasAuthority('research:projectType:edit')")
    @Log(title = "项目类型", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchProjectType type) {
        typeService.update(type);
        return AjaxResult.success();
    }

    @Operation(summary = "删除项目类型")
    @DeleteMapping("/{typeIds}")
    @PreAuthorize("hasAuthority('research:projectType:remove')")
    @Log(title = "项目类型", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] typeIds) {
        List<Long> ids = Arrays.asList(typeIds);
        typeService.deleteByIds(ids);
        return AjaxResult.success();
    }
}

