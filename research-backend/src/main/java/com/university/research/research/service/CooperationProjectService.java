package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.CooperationProject;
import com.university.research.research.domain.CooperationProjectApproval;

import java.util.List;

public interface CooperationProjectService {

    Page<CooperationProject> selectPage(Page<CooperationProject> page, CooperationProject query);

    CooperationProject selectById(Long coopProjectId);

    void createDraft(CooperationProject project);

    void update(CooperationProject project);

    void submit(Long coopProjectId);

    void approve(Long coopProjectId, String decision, String comment);

    List<CooperationProjectApproval> selectApprovals(Long coopProjectId);

    void deleteByIds(List<Long> ids);
}
