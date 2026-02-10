package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.CooperationProject;
import com.university.research.research.domain.CooperationProjectApproval;
import com.university.research.research.service.CooperationProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "校企合作-项目管理")
@RestController
@RequestMapping("/api/research/cooperation/project")
@Validated
public class CooperationProjectController {

    @Autowired
    private CooperationProjectService projectService;

    @Operation(summary = "合作项目分页查询")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('cooperation:project:list')")
    public AjaxResult<PageResult<CooperationProject>> list(CooperationProject query, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<CooperationProject> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<CooperationProject> result = projectService.selectPage(page, query);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "合作项目详情")
    @GetMapping("/{coopProjectId}")
    @PreAuthorize("hasAuthority('cooperation:project:query')")
    public AjaxResult<CooperationProject> getInfo(@PathVariable Long coopProjectId) {
        return AjaxResult.success(projectService.selectById(coopProjectId));
    }

    @Operation(summary = "新增合作项目草稿")
    @PostMapping
    @PreAuthorize("hasAuthority('cooperation:project:add')")
    @Log(title = "校企合作项目", businessType = 1)
    public AjaxResult<Void> add(@Valid @RequestBody CooperationProject project) {
        projectService.createDraft(project);
        return AjaxResult.success();
    }

    @Operation(summary = "修改合作项目")
    @PutMapping
    @PreAuthorize("hasAuthority('cooperation:project:edit')")
    @Log(title = "校企合作项目", businessType = 2)
    public AjaxResult<Void> edit(@Valid @RequestBody CooperationProject project) {
        projectService.update(project);
        return AjaxResult.success();
    }

    @Operation(summary = "提交合作项目")
    @PostMapping("/submit/{coopProjectId}")
    @PreAuthorize("hasAuthority('cooperation:project:submit')")
    @Log(title = "校企合作项目提交", businessType = 4)
    public AjaxResult<Void> submit(@PathVariable Long coopProjectId) {
        projectService.submit(coopProjectId);
        return AjaxResult.success();
    }

    @Operation(summary = "审批合作项目")
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('cooperation:project:approve')")
    @Log(title = "校企合作项目审批", businessType = 4)
    public AjaxResult<Void> approve(@Valid @RequestBody ApprovalRequest request) {
        projectService.approve(request.getCoopProjectId(), request.getDecision(), request.getComment());
        return AjaxResult.success();
    }

    @Operation(summary = "审批记录")
    @GetMapping("/approve/records")
    @PreAuthorize("hasAuthority('cooperation:project:approve:list')")
    public AjaxResult<List<CooperationProjectApproval>> records(@RequestParam Long coopProjectId) {
        return AjaxResult.success(projectService.selectApprovals(coopProjectId));
    }

    @Operation(summary = "删除合作项目")
    @DeleteMapping("/{coopProjectIds}")
    @PreAuthorize("hasAuthority('cooperation:project:remove')")
    @Log(title = "校企合作项目", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] coopProjectIds) {
        projectService.deleteByIds(Arrays.asList(coopProjectIds));
        return AjaxResult.success();
    }

    public static class ApprovalRequest {
        @NotNull(message = "项目ID不能为空")
        private Long coopProjectId;

        @NotBlank(message = "审批结果不能为空")
        private String decision;

        @NotBlank(message = "审批意见不能为空")
        private String comment;

        public Long getCoopProjectId() {
            return coopProjectId;
        }

        public void setCoopProjectId(Long coopProjectId) {
            this.coopProjectId = coopProjectId;
        }

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
