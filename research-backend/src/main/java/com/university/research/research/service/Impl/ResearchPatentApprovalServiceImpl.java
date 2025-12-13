package com.university.research.research.service.Impl;

import com.university.research.research.domain.ResearchPatentApproval;
import com.university.research.research.mapper.ResearchPatentApprovalMapper;
import com.university.research.research.service.ResearchPatentApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchPatentApprovalServiceImpl implements ResearchPatentApprovalService {

    @Autowired
    private ResearchPatentApprovalMapper approvalMapper;

    @Override
    public List<ResearchPatentApproval> selectApprovalList(Long patentId) {
        return approvalMapper.selectApprovalList(patentId);
    }

    @Override
    public void insert(ResearchPatentApproval approval) {
        approvalMapper.insert(approval);
    }
}

