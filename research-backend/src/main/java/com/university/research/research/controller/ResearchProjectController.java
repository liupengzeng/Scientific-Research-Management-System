package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchProject;
import com.university.research.research.domain.ResearchProjectApproval;
import com.university.research.research.domain.ResearchProjectCheck;
import com.university.research.research.domain.ResearchProjectFinal;
import com.university.research.research.domain.ResearchProjectMember;
import com.university.research.research.service.ResearchProjectApprovalService;
import com.university.research.research.service.ResearchProjectCheckService;
import com.university.research.research.service.ResearchProjectFinalService;
import com.university.research.research.service.ResearchProjectMemberService;
import com.university.research.research.service.ResearchProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "科研项目管理", description = "科研项目基础功能")
@RestController
@RequestMapping("/api/research/project")
@Validated
public class ResearchProjectController {

    @Autowired
    private ResearchProjectService projectService;

    @Autowired
    private ResearchProjectApprovalService approvalService;

    @Autowired
    private ResearchProjectCheckService checkService;

    @Autowired
    private ResearchProjectFinalService finalService;

    @Autowired
    private ResearchProjectMemberService memberService;

    @Operation(summary = "分页查询项目")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:project:list')")
    public AjaxResult<PageResult<ResearchProject>> list(ResearchProject query, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchProject> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchProject> result = projectService.selectPage(page, query);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "项目详情")
    @GetMapping("/{projectId}")
    @PreAuthorize("hasAuthority('research:project:query')")
    public AjaxResult<ResearchProject> getInfo(@PathVariable Long projectId) {
        return AjaxResult.success(projectService.selectById(projectId));
    }

    @Operation(summary = "新增项目（草稿）")
    @PostMapping
    @PreAuthorize("hasAuthority('research:project:add')")
    @Log(title = "科研项目", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchProject project) {
        projectService.createDraft(project);
        return AjaxResult.success();
    }

    @Operation(summary = "更新项目")
    @PutMapping
    @PreAuthorize("hasAuthority('research:project:edit')")
    @Log(title = "科研项目", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchProject project) {
        projectService.updateProject(project);
        return AjaxResult.success();
    }

    @Operation(summary = "提交项目")
    @PostMapping("/submit/{projectId}")
    @PreAuthorize("hasAuthority('research:project:submit')")
    @Log(title = "科研项目提交", businessType = 4)
    public AjaxResult<Void> submit(@PathVariable @NotNull Long projectId) {
        projectService.submitProject(projectId);
        return AjaxResult.success();
    }

    @Operation(summary = "项目启动（立项后进入进行中）")
    @PostMapping("/start/{projectId}")
    @PreAuthorize("hasAuthority('research:project:start')")
    @Log(title = "项目启动", businessType = 4)
    public AjaxResult<Void> start(@PathVariable @NotNull Long projectId) {
        projectService.startProject(projectId);
        return AjaxResult.success();
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{projectIds}")
    @PreAuthorize("hasAuthority('research:project:remove')")
    @Log(title = "科研项目", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] projectIds) {
        List<Long> ids = Arrays.asList(projectIds);
        projectService.deleteByIds(ids);
        return AjaxResult.success();
    }

    @Operation(summary = "审批项目")
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('research:project:approve')")
    @Log(title = "项目审批", businessType = 4)
    public AjaxResult<Void> approve(@Validated @RequestBody ApprovalRequest request) {
        projectService.approveProject(request.getProjectId(), request.getDecision(), request.getComment());
        return AjaxResult.success();
    }

    @Operation(summary = "查询审批记录")
    @GetMapping("/approve/records")
    @PreAuthorize("hasAuthority('research:project:approve:list')")
    public AjaxResult<List<ResearchProjectApproval>> getApprovalRecords(@RequestParam Long projectId) {
        List<ResearchProjectApproval> records = approvalService.selectByProjectId(projectId);
        return AjaxResult.success(records);
    }

    @Operation(summary = "提交中检")
    @PostMapping("/midCheck")
    @PreAuthorize("hasAuthority('research:project:midCheck')")
    @Log(title = "项目中检", businessType = 1)
    public AjaxResult<Void> submitMidCheck(@Validated @RequestBody ResearchProjectCheck check) {
        projectService.submitMidCheck(check);
        return AjaxResult.success();
    }

    @Operation(summary = "中检记录")
    @GetMapping("/midCheck/list")
    @PreAuthorize("hasAuthority('research:project:midCheck:list')")
    public AjaxResult<PageResult<ResearchProjectCheck>> midCheckList(@RequestParam Long projectId, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchProjectCheck> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchProjectCheck> result = checkService.selectPage(page, projectId);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "提交结题")
    @PostMapping("/finalize")
    @PreAuthorize("hasAuthority('research:project:finalize')")
    @Log(title = "项目结题", businessType = 1)
    public AjaxResult<Void> submitFinal(@Validated @RequestBody ResearchProjectFinal fin) {
        projectService.submitFinal(fin);
        return AjaxResult.success();
    }

    @Operation(summary = "结题记录")
    @GetMapping("/finalize/list")
    @PreAuthorize("hasAuthority('research:project:finalize:list')")
    public AjaxResult<PageResult<ResearchProjectFinal>> finalList(@RequestParam Long projectId, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchProjectFinal> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchProjectFinal> result = finalService.selectPage(page, projectId);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "结题验收")
    @PostMapping("/finalize/accept")
    @PreAuthorize("hasAuthority('research:project:finalize:accept')")
    @Log(title = "结题验收", businessType = 4)
    public AjaxResult<Void> acceptFinal(@Validated @RequestBody AcceptRequest req) {
        projectService.acceptFinal(req.getProjectId(), req.getDecision(), req.getComment());
        return AjaxResult.success();
    }

    @Operation(summary = "查询项目成员列表")
    @GetMapping("/member/list")
    @PreAuthorize("hasAuthority('research:project:member:list')")
    public AjaxResult<List<ResearchProjectMember>> getMemberList(@RequestParam Long projectId) {
        List<ResearchProjectMember> members = memberService.selectByProjectId(projectId);
        return AjaxResult.success(members);
    }

    @Operation(summary = "添加项目成员")
    @PostMapping("/member")
    @PreAuthorize("hasAuthority('research:project:member:add')")
    @Log(title = "项目成员管理", businessType = 1)
    public AjaxResult<Void> addMember(@Validated @RequestBody ResearchProjectMember member) {
        memberService.addMember(member);
        return AjaxResult.success();
    }

    @Operation(summary = "更新项目成员")
    @PutMapping("/member")
    @PreAuthorize("hasAuthority('research:project:member:edit')")
    @Log(title = "项目成员管理", businessType = 2)
    public AjaxResult<Void> updateMember(@Validated @RequestBody ResearchProjectMember member) {
        memberService.updateMember(member);
        return AjaxResult.success();
    }

    @Operation(summary = "删除项目成员")
    @DeleteMapping("/member/{id}")
    @PreAuthorize("hasAuthority('research:project:member:remove')")
    @Log(title = "项目成员管理", businessType = 3)
    public AjaxResult<Void> removeMember(@PathVariable Long id) {
        memberService.removeMember(id);
        return AjaxResult.success();
    }

    /**
     * 审批请求DTO
     */
    public static class ApprovalRequest {
        @NotNull(message = "项目ID不能为空")
        private Long projectId;

        @NotBlank(message = "审批决定不能为空")
        private String decision; // approve / reject

        @NotBlank(message = "审批意见不能为空")
        private String comment;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
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

    /**
     * 结题验收请求
     */
    public static class AcceptRequest {
        @NotNull(message = "项目ID不能为空")
        private Long projectId;

        @NotBlank(message = "验收决定不能为空")
        private String decision; // passed / rejected

        @NotBlank(message = "验收意见不能为空")
        private String comment;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
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

