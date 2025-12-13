package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchAward;
import com.university.research.research.service.ResearchAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "获奖管理", description = "获奖管理相关接口")
@RestController
@RequestMapping("/research/award")
public class ResearchAwardController {

    @Autowired
    private ResearchAwardService awardService;

    @Operation(summary = "分页查询获奖列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:award:list')")
    public AjaxResult<PageResult<ResearchAward>> list(ResearchAward award, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchAward> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchAward> result = awardService.selectPage(page, award);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "查询获奖详情")
    @GetMapping("/{awardId}")
    @PreAuthorize("hasAuthority('research:award:query')")
    public AjaxResult<ResearchAward> getInfo(@PathVariable Long awardId) {
        return AjaxResult.success(awardService.selectById(awardId));
    }

    @Operation(summary = "新增获奖")
    @PostMapping
    @PreAuthorize("hasAuthority('research:award:add')")
    @Log(title = "获奖管理", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchAward award) {
        awardService.insert(award);
        return AjaxResult.success();
    }

    @Operation(summary = "更新获奖")
    @PutMapping
    @PreAuthorize("hasAuthority('research:award:edit')")
    @Log(title = "获奖管理", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchAward award) {
        awardService.update(award);
        return AjaxResult.success();
    }

    @Operation(summary = "删除获奖")
    @DeleteMapping("/{awardIds}")
    @PreAuthorize("hasAuthority('research:award:remove')")
    @Log(title = "获奖管理", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] awardIds) {
        awardService.deleteByIds(awardIds);
        return AjaxResult.success();
    }
}

