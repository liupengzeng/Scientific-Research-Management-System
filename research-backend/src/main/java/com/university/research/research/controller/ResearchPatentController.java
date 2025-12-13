package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchPatent;
import com.university.research.research.domain.ResearchPatentApproval;
import com.university.research.research.service.ResearchPatentApprovalService;
import com.university.research.research.service.ResearchPatentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专利管理", description = "专利管理相关接口")
@RestController
@RequestMapping("/research/patent")
public class ResearchPatentController {

    @Autowired
    private ResearchPatentService patentService;

    @Autowired
    private ResearchPatentApprovalService approvalService;

    @Operation(summary = "分页查询专利列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:patent:list')")
    public AjaxResult<PageResult<ResearchPatent>> list(ResearchPatent patent, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchPatent> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchPatent> result = patentService.selectPage(page, patent);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "查询专利详情")
    @GetMapping("/{patentId}")
    @PreAuthorize("hasAuthority('research:patent:query')")
    public AjaxResult<ResearchPatent> getInfo(@PathVariable Long patentId) {
        return AjaxResult.success(patentService.selectById(patentId));
    }

    @Operation(summary = "新增专利（草稿）")
    @PostMapping
    @PreAuthorize("hasAuthority('research:patent:add')")
    @Log(title = "专利管理", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchPatent patent) {
        patentService.createDraft(patent);
        return AjaxResult.success();
    }

    @Operation(summary = "更新专利")
    @PutMapping
    @PreAuthorize("hasAuthority('research:patent:edit')")
    @Log(title = "专利管理", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchPatent patent) {
        patentService.updatePatent(patent);
        return AjaxResult.success();
    }

    @Operation(summary = "提交专利")
    @PostMapping("/submit/{patentId}")
    @PreAuthorize("hasAuthority('research:patent:submit')")
    @Log(title = "专利提交", businessType = 4)
    public AjaxResult<Void> submit(@PathVariable @NotNull Long patentId) {
        patentService.submitPatent(patentId);
        return AjaxResult.success();
    }

    @Operation(summary = "专利审核")
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('research:patent:approve')")
    @Log(title = "专利审核", businessType = 4)
    public AjaxResult<Void> approve(@Validated @RequestBody ApprovalRequest req) {
        patentService.approvePatent(req.getPatentId(), req.getDecision(), req.getComment(), req.getFinalFlag());
        return AjaxResult.success();
    }

    @Operation(summary = "查询审核记录")
    @GetMapping("/approve/records")
    @PreAuthorize("hasAuthority('research:patent:query')")
    public AjaxResult<List<ResearchPatentApproval>> getApprovalRecords(@RequestParam Long patentId) {
        List<ResearchPatentApproval> records = approvalService.selectApprovalList(patentId);
        return AjaxResult.success(records);
    }

    @Operation(summary = "删除专利")
    @DeleteMapping("/{patentIds}")
    @PreAuthorize("hasAuthority('research:patent:remove')")
    @Log(title = "专利管理", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] patentIds) {
        patentService.deleteByIds(patentIds);
        return AjaxResult.success();
    }

    /**
     * 审核请求DTO
     */
    public static class ApprovalRequest {
        @NotNull(message = "专利ID不能为空")
        private Long patentId;

        @NotNull(message = "审批决定不能为空")
        private String decision; // approve / reject

        @NotNull(message = "审批意见不能为空")
        private String comment;

        private Integer finalFlag; // 是否终审(1是 0否)

        public Long getPatentId() {
            return patentId;
        }

        public void setPatentId(Long patentId) {
            this.patentId = patentId;
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

        public Integer getFinalFlag() {
            return finalFlag;
        }

        public void setFinalFlag(Integer finalFlag) {
            this.finalFlag = finalFlag;
        }
    }
}

