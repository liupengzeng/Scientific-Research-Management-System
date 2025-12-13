package com.university.research.research.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.research.research.domain.ResearchProject;
import com.university.research.research.domain.ResearchProjectCheck;
import com.university.research.research.domain.ResearchProjectFinal;

import java.util.List;

public interface ResearchProjectService {

    Page<ResearchProject> selectPage(Page<ResearchProject> page, ResearchProject query);

    ResearchProject selectById(Long projectId);

    void createDraft(ResearchProject project);

    void updateProject(ResearchProject project);

    void submitProject(Long projectId);

    void deleteByIds(List<Long> ids);

    void approveProject(Long projectId, String decision, String comment);

    void startProject(Long projectId);

    void submitMidCheck(ResearchProjectCheck check);

    void submitFinal(ResearchProjectFinal fin);

    void acceptFinal(Long projectId, String decision, String comment);
}

