package com.university.research.research.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.annotation.Log;
import com.university.research.common.core.AjaxResult;
import com.university.research.common.core.PageQuery;
import com.university.research.common.core.PageResult;
import com.university.research.research.domain.ResearchPaper;
import com.university.research.research.domain.ResearchPaperApproval;
import com.university.research.research.service.ResearchPaperApprovalService;
import com.university.research.research.service.ResearchPaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "论文管理", description = "论文管理相关接口")
@RestController
@RequestMapping("/research/paper")
public class ResearchPaperController {

    @Autowired
    private ResearchPaperService paperService;

    @Autowired
    private ResearchPaperApprovalService approvalService;

    @Operation(summary = "分页查询论文列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('research:paper:list')")
    public AjaxResult<PageResult<ResearchPaper>> list(ResearchPaper paper, PageQuery pageQuery) {
        pageQuery.checkPage();
        Page<ResearchPaper> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<ResearchPaper> result = paperService.selectPage(page, paper);
        return AjaxResult.page(PageResult.of(result));
    }

    @Operation(summary = "查询论文详情")
    @GetMapping("/{paperId}")
    @PreAuthorize("hasAuthority('research:paper:query')")
    public AjaxResult<ResearchPaper> getInfo(@PathVariable Long paperId) {
        return AjaxResult.success(paperService.selectById(paperId));
    }

    @Operation(summary = "新增论文（草稿）")
    @PostMapping
    @PreAuthorize("hasAuthority('research:paper:add')")
    @Log(title = "论文管理", businessType = 1)
    public AjaxResult<Void> add(@Validated @RequestBody ResearchPaper paper) {
        paperService.createDraft(paper);
        return AjaxResult.success();
    }

    @Operation(summary = "更新论文")
    @PutMapping
    @PreAuthorize("hasAuthority('research:paper:edit')")
    @Log(title = "论文管理", businessType = 2)
    public AjaxResult<Void> edit(@Validated @RequestBody ResearchPaper paper) {
        paperService.updatePaper(paper);
        return AjaxResult.success();
    }

    @Operation(summary = "提交论文")
    @PostMapping("/submit/{paperId}")
    @PreAuthorize("hasAuthority('research:paper:submit')")
    @Log(title = "论文提交", businessType = 4)
    public AjaxResult<Void> submit(@PathVariable @NotNull Long paperId) {
        paperService.submitPaper(paperId);
        return AjaxResult.success();
    }

    @Operation(summary = "论文审核")
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('research:paper:approve')")
    @Log(title = "论文审核", businessType = 4)
    public AjaxResult<Void> approve(@Validated @RequestBody ApprovalRequest req) {
        paperService.approvePaper(req.getPaperId(), req.getDecision(), req.getComment(), req.getFinalFlag());
        return AjaxResult.success();
    }

    @Operation(summary = "查询审核记录")
    @GetMapping("/approve/records")
    @PreAuthorize("hasAuthority('research:paper:query')")
    public AjaxResult<List<ResearchPaperApproval>> getApprovalRecords(@RequestParam Long paperId) {
        List<ResearchPaperApproval> records = approvalService.selectApprovalList(paperId);
        return AjaxResult.success(records);
    }

    @Operation(summary = "删除论文")
    @DeleteMapping("/{paperIds}")
    @PreAuthorize("hasAuthority('research:paper:remove')")
    @Log(title = "论文管理", businessType = 3)
    public AjaxResult<Void> remove(@PathVariable Long[] paperIds) {
        paperService.deleteByIds(paperIds);
        return AjaxResult.success();
    }

    /**
     * 审核请求DTO
     */
    public static class ApprovalRequest {
        @NotNull(message = "论文ID不能为空")
        private Long paperId;

        @NotNull(message = "审批决定不能为空")
        private String decision; // approve / reject

        @NotNull(message = "审批意见不能为空")
        private String comment;

        private Integer finalFlag; // 是否终审(1是 0否)

        public Long getPaperId() {
            return paperId;
        }

        public void setPaperId(Long paperId) {
            this.paperId = paperId;
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

