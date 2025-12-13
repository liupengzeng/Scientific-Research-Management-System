package com.university.research.research.service;

import com.university.research.research.domain.ResearchPaperApproval;

import java.util.List;

public interface ResearchPaperApprovalService {

    List<ResearchPaperApproval> selectApprovalList(Long paperId);

    void insert(ResearchPaperApproval approval);
}

