package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.CooperationEnterprise;
import com.university.research.research.service.CooperationEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Tag(name = "校企合作-企业管理")
@RestController
@RequestMapping("/api/research/cooperation/enterprise")
public class CooperationEnterpriseController {

    @Autowired
    private CooperationEnterpriseService enterpriseService;

    @Operation(summary = "企业分页查询")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('cooperation:enterprise:list')")
    public AjaxResult<PageResult<CooperationEnterprise>> list(CooperationEnterprise query, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<CooperationEnterprise> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<CooperationEnterprise> result = enterpriseService.selectPage(page, query);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "企业详情")
    @GetMapping("/{enterpriseId}")
    @PreAuthorize("hasAuthority('cooperation:enterprise:query')")
    public AjaxResult<CooperationEnterprise> getInfo(@PathVariable Long enterpriseId) {
        return AjaxResult.success(enterpriseService.selectById(enterpriseId));
    }

    @Operation(summary = "新增企业")
    @PostMapping
    @PreAuthorize("hasAuthority('cooperation:enterprise:add')")
    @Log(title = "校企合作企业", businessType = 1)
    public AjaxResult<Void> add(@Valid @RequestBody CooperationEnterprise enterprise) {
        enterpriseService.insert(enterprise);
        return AjaxResult.success();
    }

    @Operation(summary = "修改企业")
    @PutMapping
    @PreAuthorize("hasAuthority('cooperation:enterprise:edit')")
    @Log(title = "校企合作企业", businessType = 2)
    public AjaxResult<Void> edit(@Valid @RequestBody CooperationEnterprise enterprise) {
        enterpriseService.update(enterprise);
        return AjaxResult.success();
    }

    @Operation(summary = "删除企业")
    @DeleteMapping("/{enterpriseIds}")
    @PreAuthorize("hasAuthority('cooperation:enterprise:remove')")
    @Log(title = "校企合作企业", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] enterpriseIds) {
        enterpriseService.deleteByIds(Arrays.asList(enterpriseIds));
        return AjaxResult.success();
    }
}
