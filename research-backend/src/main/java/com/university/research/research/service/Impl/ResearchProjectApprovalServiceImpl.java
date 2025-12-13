package com.university.research.research.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectApproval;
import com.university.research.research.mapper.ResearchProjectApprovalMapper;
import com.university.research.research.service.ResearchProjectApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchProjectApprovalServiceImpl implements ResearchProjectApprovalService {

    @Autowired
    private ResearchProjectApprovalMapper approvalMapper;

    @Override
    public void insert(ResearchProjectApproval approval) {
        approvalMapper.insert(approval);
    }

    @Override
    public Page<ResearchProjectApproval> selectApprovalPage(Page<ResearchProjectApproval> page, ResearchProjectApproval query) {
        List<ResearchProjectApproval> list = approvalMapper.selectApprovalList(page, query);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<ResearchProjectApproval> selectByProjectId(Long projectId) {
        ResearchProjectApproval query = new ResearchProjectApproval();
        query.setProjectId(projectId);
        Page<ResearchProjectApproval> page = new Page<>(1, 1000);
        return approvalMapper.selectApprovalList(page, query);
    }
}

