package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.research.domain.ResearchPaper;
import com.university.research.research.domain.ResearchPaperApproval;
import com.university.research.research.mapper.ResearchPaperMapper;
import com.university.research.research.service.ResearchPaperApprovalService;
import com.university.research.research.service.ResearchPaperService;
import com.university.research.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchPaperServiceImpl implements ResearchPaperService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_SUBMITTED = "submitted";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";

    @Autowired
    private ResearchPaperMapper paperMapper;

    @Autowired
    private ResearchPaperApprovalService approvalService;

    @Override
    public Page<ResearchPaper> selectPage(Page<ResearchPaper> page, ResearchPaper paper) {
        List<ResearchPaper> list = paperMapper.selectPaperList(paper);
        // 手动分页
        int total = list.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        if (start < total) {
            List<ResearchPaper> pageList = list.subList(start, end);
            page.setRecords(pageList);
            page.setTotal(total);
        } else {
            page.setRecords(List.of());
            page.setTotal(total);
        }
        return page;
    }

    @Override
    public ResearchPaper selectById(Long paperId) {
        return paperMapper.selectById(paperId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDraft(ResearchPaper paper) {
        paper.setStatus(STATUS_DRAFT);
        paperMapper.insert(paper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaper(ResearchPaper paper) {
        if (paper.getPaperId() == null) {
            throw new ServiceException("论文ID不能为空");
        }
        ResearchPaper existing = paperMapper.selectById(paper.getPaperId());
        if (existing == null) {
            throw new ServiceException("论文不存在");
        }
        // 只有草稿状态才能编辑
        if (!STATUS_DRAFT.equals(existing.getStatus())) {
            throw new ServiceException("只有草稿状态的论文才能编辑");
        }
        paperMapper.updateById(paper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPaper(Long paperId) {
        ResearchPaper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new ServiceException("论文不存在");
        }
        if (!STATUS_DRAFT.equals(paper.getStatus())) {
            throw new ServiceException("只有草稿状态的论文才能提交");
        }
        paper.setStatus(STATUS_SUBMITTED);
        paperMapper.updateById(paper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePaper(Long paperId, String decision, String comment, Integer finalFlag) {
        ResearchPaper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new ServiceException("论文不存在");
        }
        if (!STATUS_SUBMITTED.equals(paper.getStatus())) {
            throw new ServiceException("只有已提交状态的论文才能审核");
        }

        // 创建审核记录
        ResearchPaperApproval approval = new ResearchPaperApproval();
        approval.setPaperId(paperId);
        approval.setDecision(decision);
        approval.setComment(comment);
        Long approverId = SecurityUtils.getUserId();
        String approverName = SecurityUtils.getUsername();
        approval.setApproverId(approverId);
        approval.setApproverName(approverName != null ? approverName : "");
        approval.setFinalFlag(finalFlag != null ? finalFlag : 0);
        approvalService.insert(approval);

        // 更新论文状态
        if ("approve".equals(decision)) {
            if (finalFlag != null && finalFlag == 1) {
                // 终审通过
                paper.setStatus(STATUS_APPROVED);
            }
            // 非终审通过，状态保持 submitted，等待下一级审核
        } else if ("reject".equals(decision)) {
            // 驳回，回到草稿状态
            paper.setStatus(STATUS_REJECTED);
        }

        paperMapper.updateById(paper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] paperIds) {
        if (paperIds == null || paperIds.length == 0) {
            throw new ServiceException("论文ID不能为空");
        }
        for (Long paperId : paperIds) {
            paperMapper.deleteById(paperId);
        }
    }
}

