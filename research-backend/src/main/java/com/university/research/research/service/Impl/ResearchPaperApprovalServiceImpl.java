package com.university.research.research.service.Impl;

import com.university.research.research.domain.ResearchPaperApproval;
import com.university.research.research.mapper.ResearchPaperApprovalMapper;
import com.university.research.research.service.ResearchPaperApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchPaperApprovalServiceImpl implements ResearchPaperApprovalService {

    @Autowired
    private ResearchPaperApprovalMapper approvalMapper;

    @Override
    public List<ResearchPaperApproval> selectApprovalList(Long paperId) {
        return approvalMapper.selectApprovalList(paperId);
    }

    @Override
    public void insert(ResearchPaperApproval approval) {
        approvalMapper.insert(approval);
    }
}

