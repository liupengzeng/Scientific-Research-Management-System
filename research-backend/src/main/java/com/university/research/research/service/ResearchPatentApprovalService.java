package com.university.research.research.service;

import com.university.research.research.domain.ResearchPatentApproval;

import java.util.List;

public interface ResearchPatentApprovalService {

    List<ResearchPatentApproval> selectApprovalList(Long patentId);

    void insert(ResearchPatentApproval approval);
}

