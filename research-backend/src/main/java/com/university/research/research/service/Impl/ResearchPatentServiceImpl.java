package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.common.exception.ServiceException;
import com.university.research.common.utils.SecurityUtils;
import com.university.research.research.domain.ResearchPatent;
import com.university.research.research.domain.ResearchPatentApproval;
import com.university.research.research.mapper.ResearchPatentMapper;
import com.university.research.research.service.ResearchPatentApprovalService;
import com.university.research.research.service.ResearchPatentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResearchPatentServiceImpl implements ResearchPatentService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_SUBMITTED = "submitted";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";

    @Autowired
    private ResearchPatentMapper patentMapper;

    @Autowired
    private ResearchPatentApprovalService approvalService;

    @Override
    public Page<ResearchPatent> selectPage(Page<ResearchPatent> page, ResearchPatent patent) {
        List<ResearchPatent> list = patentMapper.selectPatentList(patent);
        // 手动分页
        int total = list.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        if (start < total) {
            List<ResearchPatent> pageList = list.subList(start, end);
            page.setRecords(pageList);
            page.setTotal(total);
        } else {
            page.setRecords(List.of());
            page.setTotal(total);
        }
        return page;
    }

    @Override
    public ResearchPatent selectById(Long patentId) {
        return patentMapper.selectById(patentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDraft(ResearchPatent patent) {
        patent.setStatus(STATUS_DRAFT);
        patentMapper.insert(patent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePatent(ResearchPatent patent) {
        if (patent.getPatentId() == null) {
            throw new ServiceException("专利ID不能为空");
        }
        ResearchPatent existing = patentMapper.selectById(patent.getPatentId());
        if (existing == null) {
            throw new ServiceException("专利不存在");
        }
        // 只有草稿状态才能编辑
        if (!STATUS_DRAFT.equals(existing.getStatus())) {
            throw new ServiceException("只有草稿状态的专利才能编辑");
        }
        patentMapper.updateById(patent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPatent(Long patentId) {
        ResearchPatent patent = patentMapper.selectById(patentId);
        if (patent == null) {
            throw new ServiceException("专利不存在");
        }
        if (!STATUS_DRAFT.equals(patent.getStatus())) {
            throw new ServiceException("只有草稿状态的专利才能提交");
        }
        patent.setStatus(STATUS_SUBMITTED);
        patentMapper.updateById(patent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePatent(Long patentId, String decision, String comment, Integer finalFlag) {
        ResearchPatent patent = patentMapper.selectById(patentId);
        if (patent == null) {
            throw new ServiceException("专利不存在");
        }
        if (!STATUS_SUBMITTED.equals(patent.getStatus())) {
            throw new ServiceException("只有已提交状态的专利才能审核");
        }

        // 创建审核记录
        ResearchPatentApproval approval = new ResearchPatentApproval();
        approval.setPatentId(patentId);
        approval.setDecision(decision);
        approval.setComment(comment);
        Long approverId = SecurityUtils.getUserId();
        String approverName = SecurityUtils.getUsername();
        approval.setApproverId(approverId);
        approval.setApproverName(approverName != null ? approverName : "");
        approval.setFinalFlag(finalFlag != null ? finalFlag : 0);
        approvalService.insert(approval);

        // 更新专利状态
        if ("approve".equals(decision)) {
            if (finalFlag != null && finalFlag == 1) {
                // 终审通过
                patent.setStatus(STATUS_APPROVED);
            }
            // 非终审通过，状态保持 submitted，等待下一级审核
        } else if ("reject".equals(decision)) {
            // 驳回，回到草稿状态
            patent.setStatus(STATUS_REJECTED);
        }

        patentMapper.updateById(patent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(Long[] patentIds) {
        if (patentIds == null || patentIds.length == 0) {
            throw new ServiceException("专利ID不能为空");
        }
        for (Long patentId : patentIds) {
            patentMapper.deleteById(patentId);
        }
    }
}

