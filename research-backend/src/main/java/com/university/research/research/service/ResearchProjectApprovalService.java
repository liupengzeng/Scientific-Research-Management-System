package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProjectApproval;

import java.util.List;

public interface ResearchProjectApprovalService {

    void insert(ResearchProjectApproval approval);

    Page<ResearchProjectApproval> selectApprovalPage(Page<ResearchProjectApproval> page, ResearchProjectApproval query);

    List<ResearchProjectApproval> selectByProjectId(Long projectId);
}

